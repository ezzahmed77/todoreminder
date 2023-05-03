package com.example.android.mytodo.ui.todo

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.mytodo.R
import com.example.android.mytodo.data.model.Priority
import com.example.android.mytodo.data.model.Todo
import com.example.android.mytodo.ui.theme.MyToDoTheme
import java.time.format.DateTimeFormatter

enum class FilterType(val title: Int) {
    ALL(title = R.string.all),
    TODAY(title = R.string.today),
    TOMORROW(title = R.string.tomorrow),
    NEXT_SEVEN_DAYS(title = R.string.next_seven_days),
    COMPLETED(title = R.string.completed),
    TRASH(title = R.string.trash)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodoFilteredList(
    modifier: Modifier = Modifier,
    filter: FilterType,
    onTodoClick: (Int) -> Unit,
    list: List<Todo>,
    onCheckedChange: (Todo) -> Unit,
    onSwipeLeft: (Todo) -> Unit,
    onSwipeRight: (Todo) -> Unit,
    showDetails: Boolean = true
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(items = list, key = { it.id }) { todo ->
            val state = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        onSwipeLeft(todo)
                    } else if (it == DismissValue.DismissedToEnd) {
                        onSwipeRight(todo)
                    }
                    true
                }
            )

            val icon = if (state.dismissDirection == DismissDirection.EndToStart) {
                if (filter == FilterType.TRASH) {
                    Icons.Filled.Close
                } else {
                    Icons.Filled.Delete
                }
            } else {
                Icons.Filled.ArrowForward
            }

            SwipeToDismiss(
                state = state,
                background = {
                    val color = when (state.dismissDirection) {
                        DismissDirection.EndToStart -> Color.Red
                        DismissDirection.StartToEnd -> Color.Blue
                        else -> Color.White
                    }
                    Box(
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.medium)
                            .fillMaxSize()
                            .background(color = color)
                            .padding(8.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = when (state.dismissDirection) {
                                DismissDirection.EndToStart -> Modifier.align(Alignment.CenterEnd)
                                else -> Modifier.align(Alignment.CenterStart)
                            }
                        )
                    }
                },
                directions =
                if (filter != FilterType.TRASH) {
                    setOf(DismissDirection.EndToStart)
                } else {
                    setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd)
                },

                dismissContent = {
                    TodoItem(
                        todo = todo,
                        onTodoClick = { onTodoClick(todo.id) },
                        checked = todo.isCompleted,
                        onCheckedChange = onCheckedChange,
                        showDetails = showDetails
                    )
                }
            )
        }
    }
}


@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: Todo,
    onTodoClick: (Todo) -> Unit,
    checked: Boolean,
    onCheckedChange: (Todo) -> Unit,
    showDetails: Boolean = true
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .padding(4.dp)
                .clickable {
                    if (!todo.isTrashed) {
                        onTodoClick(todo)
                    } else {
                        Toast
                            .makeText(
                                context,
                                "Can't edit item in trash!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange(todo) },
                enabled = !todo.isTrashed
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.CenterVertically),
                        onDraw = {
                            drawCircle(color = todo.priority.color)
                        }
                    )

                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    )

                    if (todo.hasDateAndTime) {
                        Text(
                            text = todo.date.format(
                                DateTimeFormatter.ofPattern("dd MMM, yyyy")
                            ),
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.body1,
                        )
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showDetails) {
                        Text(
                            text = todo.description,
                            style = MaterialTheme.typography.body2,
                            maxLines = 2,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .weight(1f)
                        )
                    }
                    if (todo.hasDateAndTime) {
                        if (todo.hasReminder) {
                            Icon(
                                painter = painterResource(id = R.drawable.bell_on),
                                contentDescription = stringResource(id = R.string.reminder_on),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colors.secondary
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.bell_off),
                                contentDescription = stringResource(id = R.string.reminder_off),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colors.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ToDoItemPreview() {
    MyToDoTheme {
        val todo = Todo(
            id = 0,
            title = "this is title",
            description = " This is description",
            priority = Priority.High,
            hasDateAndTime = true,
            hasReminder = true,
            isTrashed = false
        )
        TodoItem(todo = todo, onTodoClick = {}, checked = true, onCheckedChange = {})
    }
}



