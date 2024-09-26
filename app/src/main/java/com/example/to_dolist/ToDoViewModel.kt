package com.example.to_dolist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolist.db.ToDoDAO
import com.example.to_dolist.entity.toDoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel : ViewModel() {

    val todoDao = MainApp.todoDatabase.getDao()


//    val todoList : LiveData<List<toDoList>> = todoDao.getAllData()

    // Use StateFlow to expose Flow as state-driven data to the UI
    val todoList: StateFlow<List<toDoList>> = todoDao.getAllData()
        .flowOn(Dispatchers.IO) // Ensure Flow runs on IO thread
        .stateIn(
            scope = viewModelScope,               // Scope in which Flow runs
            started = SharingStarted.WhileSubscribed(5000),  // Start when observed
            initialValue = emptyList()            // Initial empty list
        )



    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(title : String){
        // Creating separate thread for DB operation
        viewModelScope.launch(Dispatchers.IO) {

            val maxPosition = todoDao.getMaxPosition() ?: 0

            todoDao.insertData(toDoList( title = title, date = Date.from(Instant.now()), position = maxPosition + 1 ) )
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

    fun moveTodo(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val todos = todoDao.getAllDataSync() // Get the list of todos synchronously
            if (fromIndex in todos.indices && toIndex in todos.indices) {
                // Move the item in the list
                val movedTodo = todos[fromIndex]
                val reorderedList = todos.toMutableList().apply {
                    removeAt(fromIndex)
                    add(toIndex, movedTodo)
                }

                // Update the positions in the reordered list
                reorderedList.forEachIndexed { index, todo ->
                    todoDao.updateData(todo.copy(position = index))
                }
            }
        }
    }

}