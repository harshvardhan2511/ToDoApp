package com.example.to_dolist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolist.db.ToDoDAO
import com.example.to_dolist.entity.toDoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel : ViewModel() {

    val todoDao = MainApp.todoDatabase.getDao()


    val todoList : LiveData<List<toDoList>> = todoDao.getAllData()



    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(title : String){
        // Creating separate thread for DB operation
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.insertData(toDoList( title = title, date = Date.from(Instant.now())))
        }
    }

    fun deleteTodo(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteData(id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTodo(id: Int, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = todoDao.getDataById(id)
            if (todo != null) {
                val updatedTodo = todo.copy(title = newTitle, date = Date.from(Instant.now()))
                todoDao.updateData(updatedTodo)
            }
        }
    }


}