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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.mytodo.R
import com.example.android.mytodo.data.Priority
import com.example.android.mytodo.ui.home.HomeUiState
import com.example.android.mytodo.ui.navigation.NavDestination
import com.example.android.mytodo.ui.theme.MyToDoTheme
import java.time.format.DateTimeFormatter

enum class FilterType(val title: Int){
    ALL(title = R.string.all),
    TODAY(title = R.string.today),
    TOMORROW(title = R.string.tomorrow),
    NEXT_SEVEN_DAYS(title = R.string.next_seven_days),
    COMPLETED(title = R.string.completed),
    TRASH(title = R.string.trash)
}

object AllScreenDestination: NavDestination {
    override val route: String = "all_todos"
    override val titleRes: Int = R.string.all
}

object TodayScreenDestination: NavDestination {
    override val route: String = "today_todos"
    override val titleRes: Int = R.string.today
}

object TomorrowScreenDestination: NavDestination {
    override val route: String = "tomorrow_todos"
    override val titleRes: Int = R.string.tomorrow
}

object NextWeekScreenDestination: NavDestination {
    override val route: String = "next_week_todos"
    override val titleRes: Int = R.string.next_seven_days
}

object CompletedScreenDestination: NavDestination {
    override val route: String = "completed_todos"
    override val titleRes: Int = R.string.completed
}

object TrashScreenDestination: NavDestination {
    override val route: String = "trashed_todos"
    override val titleRes: Int = R.string.trash
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ToDoFilteredList(
    modifier: Modifier = Modifier,
    filter: FilterType,
    onToDoClick: (Int) -> Unit,
    list: List<ToDoUiState>,
    onCheckedChange: (ToDoUiState) -> Unit,
    onSwipeLeft: (ToDoUiState) -> Unit,
    onSwipeRight: (ToDoUiState) -> Unit,
    showDetails: Boolean = true

){
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp).animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(items = list, key = {it.id} ) { toDoUiState ->
            val state = rememberDismissState(
                confirmStateChange = {
                    if(it == DismissValue.DismissedToStart){
                        onSwipeLeft(toDoUiState)
                    } else if(it == DismissValue.DismissedToEnd){
                        onSwipeRight(toDoUiState)
                    }
                    true
                }
            )

            val icon = if(state.dismissDirection == DismissDirection.EndToStart){
                if(filter == FilterType.TRASH){
                    Icons.Filled.Close
                }else{
                    Icons.Filled.Delete
                }
            }else{
                Icons.Filled.ArrowForward
            }

            SwipeToDismiss(
                state = state,
                background = {
                    val color = when(state.dismissDirection){
                        DismissDirection.EndToStart -> Color.Red
                        DismissDirection.StartToEnd -> Color.Blue
                        else -> Color.White
                    }
                    Box(modifier = Modifier
                        .clip(shape = MaterialTheme.shapes.medium)
                        .fillMaxSize()
                        .background(color = color)
                        .padding(8.dp),
                    ){
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = when(state.dismissDirection){
                                DismissDirection.EndToStart -> Modifier.align(Alignment.CenterEnd)
                                else -> Modifier.align(Alignment.CenterStart)
                            }
                        )
                    }
                },
                directions =
                if(filter != FilterType.TRASH){
                    setOf(DismissDirection.EndToStart)
                }else{
                    setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd)
                 },

                dismissContent = {
                    ToDoItem(
                        toDoUiState = toDoUiState,
                        onToDoClick = { onToDoClick(toDoUiState.id)},
                        checked = toDoUiState.isCompleted,
                        onCheckedChange = onCheckedChange,
                        showDetails = showDetails
                    )
                }
            )
        }
    }
}

@Composable
fun ToDoItem(
    modifier: Modifier = Modifier,
    toDoUiState: ToDoUiState,
    onToDoClick: (ToDoUiState) -> Unit,
    checked: Boolean,
    onCheckedChange: (ToDoUiState) -> Unit,
    showDetails: Boolean = true
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
    ) {
        // Here we add box
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange(toDoUiState) },
                enabled = !toDoUiState.isTrashed
            )
            Column(
                modifier  = if (toDoUiState.isCompleted) {
                    Modifier.alpha(.7f)
                } else {
                    Modifier.alpha(1f)
                }
                    .weight(1f)
                    .clickable {
                        if (!toDoUiState.isTrashed) {
                            onToDoClick(toDoUiState)
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Canvas(modifier = Modifier.size(8.dp), onDraw = {
                            drawCircle(color = toDoUiState.priority.color)
                        })
                        Text(
                            text = toDoUiState.title,
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 1,

                        )
                    }

                    if(toDoUiState.hasDateAndTime){
                        Text(
                            text = toDoUiState.date.format(
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
                ){
                    Text(
                        text = if(showDetails)toDoUiState.description  else  "",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(.7f),
                        maxLines = 1,
                        color = MaterialTheme.colors.onSurface,
                    )

                    if(toDoUiState.hasDateAndTime){
                        if(toDoUiState.hasReminder){
                            Icon(
                                painter = painterResource(id = R.drawable.bell_on),
                                contentDescription = stringResource(id = R.string.reminder_on),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colors.secondary
                            )
                        }else{
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
fun ToDoItemPreview(){
    MyToDoTheme() {
        val toDoUiState = ToDoUiState(
            id = 0,
            title = "this is title",
            description = " This is description",
            priority = Priority.High,
            hasDateAndTime = true,
            hasReminder = true,
            isTrashed = false
        )
        ToDoItem(toDoUiState = toDoUiState, onToDoClick = {}, checked = true, onCheckedChange = {})
    }
}



