package com.example.to_dolist

import android.os.Build
import android.util.Log
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

            val maxPosition = todoDao.getMaxPosition() ?: -1

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

                // Instead of fetching all todos again, update the specific item in the current state
                _todoList.value = _todoList.value.map {
                    if (it.id == id) updatedTodo else it
                }
            }
        }


    }

    fun moveTodo(fromIndex: Int, toIndex: Int) {
        Log.d("ToDoViewModel", "Moving item from index $fromIndex to index $toIndex")
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch the current list of todos
            val todos = todoDao.getAllDataSync()


            // Ensure that both indices are within the valid range
            if (fromIndex in todos.indices && toIndex in todos.indices) {
                // Create a mutable copy of the current list
                val re = todos.toMutableList()

                // Sort the list based on position after swapping
                val reorderedList = re.sortedBy { it.position }.toMutableList()


                // Swap the positions of the two items
                val fromTodo = reorderedList[fromIndex]
                val toTodo = reorderedList[toIndex]

                // Swap their positions in the list
                val tempPosition = fromTodo.position
                fromTodo.position = toTodo.position
                toTodo.position = tempPosition

                // Update both items in the database
                todoDao.updateData(fromTodo)
                todoDao.updateData(toTodo)

                // Update the in-memory list
                reorderedList[fromIndex] = fromTodo
                reorderedList[toIndex] = toTodo





                // Update the state with the sorted list
                _todoList.value = reorderedList

                withContext(Dispatchers.Main) {
                    Log.d("TodoViewModel", "Updated todoList: ${todos} ")
                }
            } else {
                // Log an error if indices are out of range
                Log.e("TodoViewModel", "Invalid indices: fromIndex = $fromIndex, toIndex = $toIndex")
            }
        }
    }
}


/*

1
2
0

*/