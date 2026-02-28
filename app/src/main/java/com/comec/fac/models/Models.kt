package com.comec.fac.models

data class Candidat(
    val name: String = "",
    val note: String = "",
    val mention: String = "B"
)

data class Resultat(
    val id: String = "",
    val concours: String = "",
    val session: String = "",
    val candidats: List<Candidat> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class Event(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val location: String = "Kinshasa",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
