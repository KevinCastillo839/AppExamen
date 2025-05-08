package com.moviles.appexamen.data

import android.content.Context
import androidx.room.Room
import kotlin.jvm.java

object DatabaseBuilder {

    private var INSTANCE: CourseDatabase? = null

    fun getInstance(context: Context): CourseDatabase {
        if (INSTANCE == null) {
            synchronized(CourseDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDatabase::class.java,
                    "course_database"
                ).fallbackToDestructiveMigration().build()
            }
        }
        return INSTANCE!!
    }
}