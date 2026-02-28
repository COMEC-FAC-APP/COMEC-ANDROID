package com.comec.fac.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.comec.fac.R
import com.comec.fac.repository.FirebaseRepository
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private val repo = FirebaseRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Date du jour
        val dateView = view.findViewById<TextView>(R.id.tv_date)
        val sdf = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH)
        dateView?.text = sdf.format(Date()).replaceFirstChar { it.uppercase() }

        // Dernières actualités
        val articlesContainer = view.findViewById<LinearLayout>(R.id.articles_container)
        repo.getArticles { articles ->
            activity?.runOnUiThread {
                articlesContainer?.removeAllViews()
                if (articles.isEmpty()) {
                    val tv = TextView(context)
                    tv.text = "Aucune actualité publiée"
                    tv.setTextColor(resources.getColor(R.color.text_muted, null))
                    articlesContainer?.addView(tv)
                } else {
                    articles.take(3).forEach { article ->
                        val itemView = layoutInflater.inflate(R.layout.item_article_small, articlesContainer, false)
                        itemView.findViewById<TextView>(R.id.tv_title)?.text = article.title
                        itemView.findViewById<TextView>(R.id.tv_category)?.text = article.category
                        itemView.findViewById<TextView>(R.id.tv_excerpt)?.text = article.excerpt
                        itemView.findViewById<TextView>(R.id.tv_emoji)?.text = article.emoji
                        articlesContainer?.addView(itemView)
                    }
                }
            }
        }
    }
}
