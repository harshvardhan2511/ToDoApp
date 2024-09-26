package com.example.to_dolist

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.to_dolist.drag.DragDropList
import com.example.to_dolist.drag.move
import com.example.to_dolist.entity.toDoList
import com.example.to_dolist.ui.theme.Purple40
import com.example.to_dolist.ui.theme.ToDoListTheme
import java.util.Date

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                TodoListPage(TodoViewModel())
                //TestDragDrop(TodoViewModel())
            }
        }
    }
}


@Composable
fun TestDragDrop(vm : TodoViewModel) {

    val items = remember { mutableStateListOf<toDoList>() }
    // Add dummy data to items
    repeat(10) {
        items.add(toDoList(it,  Date(),"hehe", 3)) // Example of adding dummy tasks
    }


        DragDropList(
            items = items,
            onMove = { from, to -> items.move(from, to) }, // Move function from DragDropListExtensions
            viewModel = vm
        )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar() {
    TopAppBar(title = {Text(text = "To-Do List App")},
        Modifier.background(color = Purple40)
    )
}
