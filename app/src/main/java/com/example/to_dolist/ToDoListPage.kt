package com.example.to_dolist

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.to_dolist.drag.DragDropList
import com.example.to_dolist.ui.theme.Purple40


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListPage(viewModel: TodoViewModel) {

    val todoList by viewModel.todoList.collectAsState()
    var inputText by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current


    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("To-Do List", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Purple40
                )
            )
        },


        content = { it ->

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(it)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        shape = RoundedCornerShape(20.dp),
                    )
                    Button(modifier = Modifier.padding(8.dp),
                        onClick = {
                            if (inputText.isNotEmpty()) {
                                viewModel.addTodo(inputText)
                                inputText = ""
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter some text",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    ) {
                        Text(text = "Add", color = Color.White)
                    }
                }

                DragDropList(
                    items = todoList,
                    onMove = { fromIndex, toIndex -> viewModel.moveTodo(fromIndex, toIndex) },
                    viewModel = viewModel
                )


            }

        }
    )
}
