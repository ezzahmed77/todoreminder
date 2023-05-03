package com.example.android.mytodo.ui.todo

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.TodoScreenAppBar
import com.example.android.mytodo.ui.AppViewModelProvider
import com.example.android.mytodo.ui.navigation.NavDestination
import java.time.format.DateTimeFormatter

object ToDoDetailsDestination : NavDestination {
    override val route = "todo_details"
    override val titleRes = R.string.item_detail_title
    const val todoIdArg = "todoId"
    val routeWithArgs = "$route/{$todoIdArg}"
}

@Composable
fun TodoDetailScreen(
    modifier: Modifier = Modifier,
    navigateToToDoEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: TodoDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TodoScreenAppBar(
                title = stringResource(id = R.string.item_detail_title),
                canNavigateBack = true,
                onNavigationIconClicked = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToToDoEdit(uiState.id) },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_todo_title),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    ) { innerPadding ->
        ToDoDetailsBody(
            toDoUiState = uiState,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ToDoDetailsBody(
    modifier: Modifier = Modifier,
    toDoUiState: TodoUiState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        ToDoDetailItem(name = "Title", value = toDoUiState.title )

        if(toDoUiState.description.isNotEmpty()){
            ToDoDetailItem(name = "Description", value = toDoUiState.description )
        }

        ToDoDetailItem(
            name = "Priority",
            value = toDoUiState.priority.name,
            valueColor = toDoUiState.priority.color
        )

        if(toDoUiState.hasDateAndTime){
            ToDoDetailItem(
                name = "Date",
                value =  toDoUiState.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            )
            ToDoDetailItem(
                name = "Time",
                value =  toDoUiState.time.format(DateTimeFormatter.ofPattern("hh:mm a"))
            )
            ToDoDetailItem(
                name = "Reminder",
                value =  if(toDoUiState.hasReminder) "On" else "Off"
            )
        }
    }
}

@Composable
fun ToDoDetailItem(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    nameColor: Color = MaterialTheme.colors.secondary,
    valueColor: Color = MaterialTheme.colors.onSurface
){
    Card(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    color = nameColor
                ),
                modifier = Modifier.width(100.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1.copy(
                    color = valueColor,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}
