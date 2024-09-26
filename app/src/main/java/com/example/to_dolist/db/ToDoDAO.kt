package com.example.to_dolist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.to_dolist.entity.toDoList
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ToDoDAO {

    @Query("SELECT * FROM todolist")
    fun getAllData() : Flow<List<toDoList>>

    @Query("SELECT * FROM toDoList WHERE id = :id LIMIT 1")
    suspend fun getDataById(id: Int): toDoList?

    @Insert
    suspend fun insertData(toDoList: toDoList)

    @Query("DELETE FROM todolist WHERE id = :id")
    suspend fun deleteData(id : Int)

    @Update
    suspend fun updateData(todo: toDoList)

    @Query("SELECT * FROM todoList")
    fun getAllDataSync(): List<toDoList>

    @Query("SELECT MAX(position) FROM todoList")
    suspend fun getMaxPosition(): Int?

}