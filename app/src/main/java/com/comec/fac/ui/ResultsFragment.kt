package com.comec.fac.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.comec.fac.R
import com.comec.fac.models.Resultat
import com.comec.fac.repository.FirebaseRepository

class ResultsFragment : Fragment() {
    private val repo = FirebaseRepository()
    private var allResults = listOf<Resultat>()
    private var currentConcours = "DEMIA"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabs = listOf(
            view.findViewById<Button>(R.id.btn_demia),
            view.findViewById<Button>(R.id.btn_dem),
            view.findViewById<Button>(R.id.btn_cfcu),
            view.findViewById<Button>(R.id.btn_cc)
        )
        val names = listOf("DEMIA", "DEM", "CFCU", "CC")

        tabs.forEachIndexed { index, btn ->
            btn?.setOnClickListener {
                currentConcours = names[index]
                tabs.forEach { b -> b?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background)) }
                btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_primary))
                renderResults(view)
            }
        }

        // Activer DEMIA par défaut
        tabs[0]?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_primary))

        repo.getResultats { results ->
            allResults = results
            activity?.runOnUiThread { renderResults(view) }
        }
    }

    private fun renderResults(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.results_container) ?: return
        container.removeAllViews()
        val filtered = allResults.filter { it.concours == currentConcours }

        if (filtered.isEmpty()) {
            val tv = TextView(context)
            tv.text = "Aucun résultat pour $currentConcours"
            tv.setTextColor(resources.getColor(R.color.text_muted, null))
            tv.setPadding(16, 32, 16, 32)
            container.addView(tv)
            return
        }

        filtered.forEach { resultat ->
            val header = TextView(context)
            header.text = "${resultat.concours} — Session ${resultat.session}"
            header.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_primary))
            header.textSize = 16f
            header.setPadding(8, 16, 8, 8)
            container.addView(header)

            resultat.candidats.forEachIndexed { idx, cand ->
                val row = layoutInflater.inflate(R.layout.item_candidat, container, false)
                row.findViewById<TextView>(R.id.tv_rank)?.text = "#${idx + 1}"
                row.findViewById<TextView>(R.id.tv_name)?.text = cand.name
                row.findViewById<TextView>(R.id.tv_note)?.text = cand.note
                row.findViewById<TextView>(R.id.tv_mention)?.text = cand.mention
                container.addView(row)
            }
        }
    }
}
