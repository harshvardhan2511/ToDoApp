package com.example.to_dolist

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_dolist.entity.toDoList
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.example.to_dolist.drag.DragDropList
import com.example.to_dolist.ui.theme.Purple40


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListPage(viewModel: TodoViewModel){

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


        content ={ it ->

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
                            modifier= Modifier.weight(2f),
                            value = inputText,
                            onValueChange = {
                                inputText = it
                            },
                            shape = RoundedCornerShape(20.dp),
                            )
                        Button(modifier = Modifier.padding(8.dp),
                            onClick = {
                            if(inputText.isNotEmpty()){
                                viewModel.addTodo(inputText)
                                inputText = ""
                            }else{
                                Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
                            }
                        }

                        ) {
                            Text(text = "Add", color = Color.White)
                        }
                    }

                    todoList.let {
                //                        LazyColumn(
                //                            content = {
                //                                itemsIndexed(it){index: Int, item: toDoList ->
                //                                    TodoItem(item = item, viewModel, onDelete = {
                //                                        viewModel.deleteTodo(item.id)
                //                                    })
                //                                }
                //                            }
                //                        )

                        DragDropList(items = it, onMove = {fromIndex, toIndex -> viewModel.moveTodo(fromIndex, toIndex)} , viewModel = viewModel)

                    }


                }

        }
    )




}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoItem(item : toDoList, viewModel: TodoViewModel ,onDelete : ()-> Unit) {

    var showDialog by remember { mutableStateOf(false) }
    val text = item.title
    var textInput by remember { mutableStateOf(TextFieldValue(text)) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = SimpleDateFormat("EEE, MMM d", Locale.ENGLISH).format(item.date),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Text(
                text = SimpleDateFormat("HH:mm:aa", Locale.ENGLISH).format(item.date),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = Color.White
            )
        }
        IconButton(onClick = { showDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_edit_24),
                contentDescription = "Edit",
                tint = Color.White
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Edit Task") },
            text = {
                // Text input field inside the dialog
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { newValue -> textInput = newValue },
                    placeholder = { Text("Enter Text") },
                    label = { Text("Enter Text") },
                    modifier = Modifier.fillMaxWidth() // Make the text field fill the dialog width
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Action on Confirm button
                        // Handle the input data (e.g., save or process the text)

                        if(textInput.text.isNotEmpty()){
                            viewModel.updateTodo(item.id, textInput.text)
                            showDialog = false
                        } else {
                            Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
                        }

                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

}