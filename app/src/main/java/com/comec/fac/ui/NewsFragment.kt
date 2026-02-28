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

class NewsFragment : Fragment() {
    private val repo = FirebaseRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val container = view.findViewById<LinearLayout>(R.id.news_container)

        repo.getArticles { articles ->
            activity?.runOnUiThread {
                container?.removeAllViews()
                if (articles.isEmpty()) {
                    val tv = TextView(context)
                    tv.text = getString(R.string.no_data)
                    tv.setTextColor(resources.getColor(R.color.text_muted, null))
                    tv.setPadding(16, 32, 16, 32)
                    container?.addView(tv)
                } else {
                    articles.forEach { article ->
                        val itemView = layoutInflater.inflate(R.layout.item_article, container, false)
                        itemView.findViewById<TextView>(R.id.tv_title)?.text = article.title
                        itemView.findViewById<TextView>(R.id.tv_category)?.text = article.category
                        itemView.findViewById<TextView>(R.id.tv_date)?.text = article.date
                        itemView.findViewById<TextView>(R.id.tv_excerpt)?.text = article.excerpt
                        itemView.findViewById<TextView>(R.id.tv_emoji)?.text = article.emoji
                        container?.addView(itemView)
                    }
                }
            }
        }
    }
}
