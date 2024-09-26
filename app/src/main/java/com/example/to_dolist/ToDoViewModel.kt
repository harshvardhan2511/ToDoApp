package com.example.to_dolist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolist.entity.toDoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.Date

class TodoViewModel : ViewModel() {

    val todoDao = MainApp.todoDatabase.getDao()


    // Use MutableStateFlow for handling the list state
    private val _todoList = MutableStateFlow<List<toDoList>>(emptyList())
    val todoList: StateFlow<List<toDoList>> = _todoList.asStateFlow()

    // Use StateFlow to expose Flow as state-driven data to the UI
    init {
        // Collect initial data from the database
        viewModelScope.launch(Dispatchers.IO) {
            _todoList.value = todoDao.getAllDataSync()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(title : String){
        // Creating separate thread for DB operation
        viewModelScope.launch(Dispatchers.IO) {

            val maxPosition = todoDao.getMaxPosition() ?: 0

            todoDao.insertData(toDoList( title = title, date = Date.from(Instant.now()), position = maxPosition + 1 ) )
            _todoList.value = todoDao.getAllDataSync()
        }
    }

    fun deleteTodo(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteData(id)
            _todoList.value = todoDao.getAllDataSync()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTodo(id: Int, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = todoDao.getDataById(id)
            if (todo != null) {
                val updatedTodo = todo.copy(title = newTitle, date = Date.from(Instant.now()))
                todoDao.updateData(updatedTodo)
                _todoList.value = todoDao.getAllDataSync()
            }
        }
    }

    fun moveTodo(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val todos = todoDao.getAllDataSync() // Ensure it fetches the list synchronously
            if (fromIndex in todos.indices && toIndex in todos.indices) {
                val movedTodo = todos[fromIndex]

                // Remove the item and add it at the new position
                val reorderedList = todos.toMutableList().apply {
                    removeAt(fromIndex)
                    add(toIndex, movedTodo)
                }

                // Update only the items whose positions changed
                reorderedList.forEachIndexed { index, todo ->
                    if (todo.position != index) {
                        todoDao.updateData(todo.copy(position = index))
                    }
                }

                _todoList.value = todoDao.getAllDataSync()

            }
        }
    }




}