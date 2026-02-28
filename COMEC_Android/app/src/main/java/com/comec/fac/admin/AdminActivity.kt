package com.comec.fac.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.comec.fac.R
import com.comec.fac.models.Article
import com.comec.fac.models.Candidat
import com.comec.fac.models.Event
import com.comec.fac.models.Resultat
import com.comec.fac.repository.FirebaseRepository

class AdminActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.title = "Administration COMEC"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupTabs()
        setupAddArticle()
        setupAddResult()
        setupAddEvent()
        loadLists()
    }

    private fun setupTabs() {
        val tabs = listOf(
            R.id.tab_articles to R.id.panel_articles,
            R.id.tab_results to R.id.panel_results,
            R.id.tab_events to R.id.panel_events
        )

        tabs.forEach { (tabId, panelId) ->
            findViewById<Button>(tabId)?.setOnClickListener {
                tabs.forEach { (_, pid) -> findViewById<LinearLayout>(pid)?.visibility = android.view.View.GONE }
                findViewById<LinearLayout>(panelId)?.visibility = android.view.View.VISIBLE
                tabs.forEach { (tid, _) -> findViewById<Button>(tid)?.setBackgroundColor(ContextCompat.getColor(this, R.color.background)) }
                findViewById<Button>(tabId)?.setBackgroundColor(ContextCompat.getColor(this, R.color.green_primary))
            }
        }
    }

    private fun setupAddArticle() {
        findViewById<Button>(R.id.btn_add_article)?.setOnClickListener {
            val title = findViewById<EditText>(R.id.et_art_title)?.text.toString().trim()
            val category = findViewById<Spinner>(R.id.sp_art_category)?.selectedItem.toString()
            val date = findViewById<EditText>(R.id.et_art_date)?.text.toString().trim()
            val excerpt = findViewById<EditText>(R.id.et_art_excerpt)?.text.toString().trim()
            val content = findViewById<EditText>(R.id.et_art_content)?.text.toString().trim()
            val emoji = findViewById<EditText>(R.id.et_art_emoji)?.text.toString().trim().ifEmpty { "📋" }

            if (title.isEmpty() || excerpt.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val article = Article(title = title, category = category, date = date, excerpt = excerpt, content = content, emoji = emoji)
            repo.addArticle(article, {
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                clearArticleForm()
                loadLists()
            }, { err -> Toast.makeText(this, err, Toast.LENGTH_SHORT).show() })
        }
    }

    private fun setupAddResult() {
        findViewById<Button>(R.id.btn_add_result)?.setOnClickListener {
            val concours = findViewById<Spinner>(R.id.sp_concours)?.selectedItem.toString()
            val session = findViewById<EditText>(R.id.et_session)?.text.toString().trim().ifEmpty { "2025" }
            val names = findViewById<EditText>(R.id.et_candidats_names)?.text.toString().trim()
            val notes = findViewById<EditText>(R.id.et_candidats_notes)?.text.toString().trim()

            if (names.isEmpty() || notes.isEmpty()) {
                Toast.makeText(this, "Ajoutez les candidats", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nameList = names.split("\n").filter { it.isNotEmpty() }
            val noteList = notes.split("\n").filter { it.isNotEmpty() }
            val candidats = nameList.zip(noteList).map { (n, note) ->
                val note_val = note.toFloatOrNull() ?: 0f
                Candidat(name = n, note = note, mention = if (note_val >= 16) "A" else if (note_val >= 12) "B" else "C")
            }.sortedByDescending { it.note.toFloatOrNull() ?: 0f }

            val resultat = Resultat(concours = concours, session = session, candidats = candidats)
            repo.addResultat(resultat, {
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                loadLists()
            }, { err -> Toast.makeText(this, err, Toast.LENGTH_SHORT).show() })
        }
    }

    private fun setupAddEvent() {
        findViewById<Button>(R.id.btn_add_event)?.setOnClickListener {
            val title = findViewById<EditText>(R.id.et_evt_title)?.text.toString().trim()
            val date = findViewById<EditText>(R.id.et_evt_date)?.text.toString().trim()
            val location = findViewById<EditText>(R.id.et_evt_location)?.text.toString().trim().ifEmpty { "Kinshasa" }
            val description = findViewById<EditText>(R.id.et_evt_description)?.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Remplissez le titre et la description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = Event(title = title, date = date, location = location, description = description)
            repo.addEvent(event, {
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                loadLists()
            }, { err -> Toast.makeText(this, err, Toast.LENGTH_SHORT).show() })
        }
    }

    private fun loadLists() {
        // Articles list
        val artList = findViewById<LinearLayout>(R.id.list_articles)
        repo.getArticles { articles ->
            runOnUiThread {
                artList?.removeAllViews()
                articles.forEach { a ->
                    val row = layoutInflater.inflate(R.layout.item_admin_row, artList, false)
                    row.findViewById<TextView>(R.id.tv_item_title)?.text = "${a.emoji} ${a.title}"
                    row.findViewById<TextView>(R.id.tv_item_sub)?.text = "${a.category} - ${a.date}"
                    row.findViewById<Button>(R.id.btn_delete)?.setOnClickListener {
                        AlertDialog.Builder(this).setMessage(getString(R.string.confirm_delete))
                            .setPositiveButton(getString(R.string.yes)) { _, _ -> repo.deleteArticle(a.id) { loadLists() } }
                            .setNegativeButton(getString(R.string.no), null).show()
                    }
                    artList?.addView(row)
                }
            }
        }

        // Events list
        val evtList = findViewById<LinearLayout>(R.id.list_events)
        repo.getEvents { events ->
            runOnUiThread {
                evtList?.removeAllViews()
                events.forEach { e ->
                    val row = layoutInflater.inflate(R.layout.item_admin_row, evtList, false)
                    row.findViewById<TextView>(R.id.tv_item_title)?.text = e.title
                    row.findViewById<TextView>(R.id.tv_item_sub)?.text = "${e.date} - ${e.location}"
                    row.findViewById<Button>(R.id.btn_delete)?.setOnClickListener {
                        AlertDialog.Builder(this).setMessage(getString(R.string.confirm_delete))
                            .setPositiveButton(getString(R.string.yes)) { _, _ -> repo.deleteEvent(e.id) { loadLists() } }
                            .setNegativeButton(getString(R.string.no), null).show()
                    }
                    evtList?.addView(row)
                }
            }
        }
    }

    private fun clearArticleForm() {
        listOf(R.id.et_art_title, R.id.et_art_date, R.id.et_art_excerpt, R.id.et_art_content, R.id.et_art_emoji).forEach {
            findViewById<EditText>(it)?.setText("")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
