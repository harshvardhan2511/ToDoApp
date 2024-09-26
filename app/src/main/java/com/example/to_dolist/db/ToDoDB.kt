package com.example.to_dolist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.to_dolist.entity.toDoList

@Database(entities = [toDoList::class], version = 1)
@TypeConverters(typeConverters::class)
abstract class ToDoDB : RoomDatabase() {

    companion object{
        const val Name = "Todo_Database"
    }

    abstract fun getDao(): ToDoDAO

}