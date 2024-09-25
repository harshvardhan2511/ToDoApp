package com.example.to_dolist

import android.app.Application
import androidx.room.Room
import com.example.to_dolist.db.ToDoDB

class MainApp : Application() {
    companion object {
        lateinit var todoDatabase: ToDoDB
    }

    override fun onCreate() {
        super.onCreate()
        todoDatabase = Room.databaseBuilder(
            applicationContext,
            ToDoDB::class.java,
            ToDoDB.Name
        ).build()
    }
}