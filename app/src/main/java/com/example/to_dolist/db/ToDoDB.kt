package com.example.to_dolist.db

import androidx.room.Database
import com.example.to_dolist.toDoList

@Database(entities = [toDoList::class], version = 1)
abstract class ToDoDB {

    companion object{
        const val Name = "Todo_Database"
    }

    abstract fun getDao(): ToDoDAO

}