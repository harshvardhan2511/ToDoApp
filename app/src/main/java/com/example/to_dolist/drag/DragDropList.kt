package com.example.to_dolist.drag

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_dolist.R
import com.example.to_dolist.TodoViewModel
import com.example.to_dolist.entity.toDoList
import com.example.to_dolist.ui.theme.iconColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DragDropList(
    items: List<toDoList>,
    onMove: (Int, Int) -> Unit,
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = onMove)

    var showDialog by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<toDoList?>(null) } // Track current item for editing
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset = offset)


                        if (overScrollJob?.isActive == true)
                            return@detectDragGesturesAfterLongPress

                        dragDropListState
                            .checkForScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overScrollJob = scope.launch {
                                    dragDropListState.lazyListState.scrollBy(it)
                                }
                            } ?: kotlin.run { overScrollJob?.cancel() }

                    },
                    onDragStart = {
                        dragDropListState.onDragStart(it)
                    },
                    onDragEnd = {
                        Log.d("DragDropList", "ended")
                        dragDropListState.onDraginterrupted() },
                    onDragCancel = { dragDropListState.onDraginterrupted() }
                )
            }
            .fillMaxSize()
            .padding(all = 10.dp),
        state = dragDropListState.lazyListState
    ) {
        // Sort items based on position before passing to the LazyColumn
        val sortedItems = items.sortedBy { it.position }

        itemsIndexed(sortedItems) { index: Int, item: toDoList ->
            Row(
                modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }
                        Modifier.graphicsLayer {
                            translationY = offsetOrNull ?: 0f
                        }
                    }
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .animateItemPlacement()
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

                IconButton(onClick = {
                    currentItem = item
                    textInput = TextFieldValue(item.title)
                    showDialog = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "Edit",
                        tint = iconColor
                    )
                }

                IconButton(onClick = { viewModel.deleteTodo(item.id) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "Delete",
                        tint = iconColor
                    )
                }
            }
        }
    }

    // Dialog for editing task
    if (showDialog && currentItem != null) {
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
                        if (textInput.text.isNotEmpty()) {
                            currentItem?.let {
                                viewModel.updateTodo(it.id, textInput.text)
                            }
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
