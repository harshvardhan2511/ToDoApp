package com.example.to_dolist.drag

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Job

@Composable
fun rememberDragDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Int, Int) -> Unit
): DragDropListState {
    return remember(lazyListState) {
        DragDropListState(lazyListState, onMove)
    }
}

class DragDropListState(
    val lazyListState: LazyListState,
    private var onMove: (Int, Int) -> Unit
){
    var draggedDistance by mutableStateOf(0f)
    var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let {
            Pair(it.offset, it.offsetEnd)
        }

    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let { draggedIndex ->
                // Find the visible item corresponding to the dragged index
                lazyListState.getVisibleItemInfoFor(absoluteIndex = draggedIndex)
//                lazyListState.layoutInfo.visibleItemsInfo.find { it.index == draggedIndex }
            }
            ?.let { visibleItem -> (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - visibleItem.offset
//                // Calculate the displacement based on the initial offset and dragged distance
//                val initialOffset = (initiallyDraggedElement?.offset ?: 0f).toFloat()
//                val currentOffset = visibleItem.offset.toFloat()
//
//                // Return the displacement as the difference between initial and current offsets
//                initialOffset + draggedDistance - currentOffset
            }

    val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let { draggedIndex ->
            // Find the visible item corresponding to the dragged index
            lazyListState.getVisibleItemInfoFor(absoluteIndex = draggedIndex)
            //lazyListState.layoutInfo.visibleItemsInfo.find { it.index == draggedIndex }
        }

    var overScrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(offset: Offset) {

        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                // Convert y offset to an Int and check if it is within the bounds of this item's vertical position
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDraginterrupted(){
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overScrollJob?.cancel()
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hoveredElement ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetEnd < startOffset || item.offset > endOffset || hoveredElement.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = startOffset - hoveredElement.offset
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset)
                        }
                    }?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
                            onMove.invoke(current, item.index)
                        }
                        currentIndexOfDraggedItem = item.index
                    }
            }
        }
    }

    fun checkForScroll(): Float{
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf {  diff ->
                    diff > 0
                }
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
                    diff < 0
                }
                else -> null
            }
        } ?: 0f
    }



}

