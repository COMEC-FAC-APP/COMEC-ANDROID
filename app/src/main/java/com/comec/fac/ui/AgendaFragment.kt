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

class AgendaFragment : Fragment() {
    private val repo = FirebaseRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = view.findViewById<LinearLayout>(R.id.agenda_container)

        repo.getEvents { events ->
            activity?.runOnUiThread {
                container?.removeAllViews()
                if (events.isEmpty()) {
                    val tv = TextView(context)
                    tv.text = "Aucun événement planifié"
                    tv.setTextColor(resources.getColor(R.color.text_muted, null))
                    tv.setPadding(16, 32, 16, 32)
                    container?.addView(tv)
                } else {
                    events.forEach { event ->
                        val itemView = layoutInflater.inflate(R.layout.item_event, container, false)
                        itemView.findViewById<TextView>(R.id.tv_title)?.text = event.title
                        itemView.findViewById<TextView>(R.id.tv_date)?.text = event.date
                        itemView.findViewById<TextView>(R.id.tv_location)?.text = event.location
                        itemView.findViewById<TextView>(R.id.tv_description)?.text = event.description
                        container?.addView(itemView)
                    }
                }
            }
        }
    }
}
