package com.moviles.appexamen.models

data class Course(
    val id: Int?,
    val name: String,
    val schedule: String,
    val professor: String,
    val description: String,
    val imageUrl: String?
)
