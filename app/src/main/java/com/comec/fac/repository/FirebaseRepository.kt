package com.comec.fac.repository

import com.comec.fac.models.Article
import com.comec.fac.models.Resultat
import com.comec.fac.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    // ===== ARTICLES =====
    fun getArticles(onResult: (List<Article>) -> Unit) {
        db.collection("articles")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Article::class.java)?.copy(id = it.id) } ?: emptyList()
                onResult(list)
            }
    }

    fun addArticle(article: Article, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("articles").add(article)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erreur") }
    }

    fun deleteArticle(id: String, onSuccess: () -> Unit) {
        db.collection("articles").document(id).delete().addOnSuccessListener { onSuccess() }
    }

    // ===== RESULTATS =====
    fun getResultats(onResult: (List<Resultat>) -> Unit) {
        db.collection("resultats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Resultat::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onResult(list)
            }
    }

    fun addResultat(resultat: Resultat, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("resultats").add(resultat)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erreur") }
    }

    fun deleteResultat(id: String, onSuccess: () -> Unit) {
        db.collection("resultats").document(id).delete().addOnSuccessListener { onSuccess() }
    }

    // ===== EVENTS =====
    fun getEvents(onResult: (List<Event>) -> Unit) {
        db.collection("events")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) } ?: emptyList()
                onResult(list)
            }
    }

    fun addEvent(event: Event, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("events").add(event)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erreur") }
    }

    fun deleteEvent(id: String, onSuccess: () -> Unit) {
        db.collection("events").document(id).delete().addOnSuccessListener { onSuccess() }
    }
}
