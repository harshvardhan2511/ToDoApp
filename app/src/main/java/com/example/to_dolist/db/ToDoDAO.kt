package com.example.to_dolist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.to_dolist.entity.toDoList
import java.util.Date

@Dao
interface ToDoDAO {

    @Query("SELECT * FROM todolist")
    fun getAllData() : LiveData<List<toDoList>>

    @Query("SELECT * FROM toDoList WHERE id = :id LIMIT 1")
    suspend fun getDataById(id: Int): toDoList?

    @Insert
    fun insertData(toDoList: toDoList)

    @Query("DELETE FROM todolist WHERE id = :id")
    fun deleteData(id : Int)

    @Update
    suspend fun updateData(todo: toDoList)

}