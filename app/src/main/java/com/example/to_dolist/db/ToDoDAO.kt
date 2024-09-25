package com.example.to_dolist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.example.to_dolist.toDoList

@Dao
interface toDoDAO {

    fun getAllData() : LiveData<List<toDoList>>

    fun insertData(toDoList: toDoList)

    fun deleteData(id : Int)

}