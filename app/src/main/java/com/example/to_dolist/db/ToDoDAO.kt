package com.example.to_dolist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.to_dolist.toDoList

@Dao
interface ToDoDAO {

    @Query("SELECT * FROM todolist")
    fun getAllData() : LiveData<List<toDoList>>

    @Insert
    fun insertData(toDoList: toDoList)

    @Query("DELETE FROM todolist WHERE id = :id")
    fun deleteData(id : Int)

}