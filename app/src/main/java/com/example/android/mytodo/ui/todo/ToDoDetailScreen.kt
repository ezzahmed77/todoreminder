package com.example.android.mytodo.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.ToDoScreenAppBar
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
fun ToDoDetailScreen(
    modifier: Modifier = Modifier,
    navigateToToDoEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ToDoDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState().value
    Scaffold(
        topBar = {
            ToDoScreenAppBar(
                title = stringResource(ToDoDetailsDestination.titleRes),
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
        },
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
    toDoUiState: ToDoUiState
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ToDoDetailItem(name = "Title:", value = toDoUiState.title )

        if(toDoUiState.description.isNotEmpty()){
            ToDoDetailItem(name = "Description:", value = toDoUiState.description )
        }

        ToDoDetailItem(
            name = "Priority:",
            value = toDoUiState.priority.name,
            valueColor = toDoUiState.priority.color
        )

        if(toDoUiState.hasDateAndTime){
            ToDoDetailItem(
                name = "Date:",
                value =  toDoUiState.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            )
            ToDoDetailItem(
                name = "Time:",
                value =  toDoUiState.time.format(DateTimeFormatter.ofPattern("hh: mm"))
            )
            ToDoDetailItem(
                name = "Reminder:",
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
    nameColor: Color = MaterialTheme.colors.onPrimary,
    valueColor: Color = MaterialTheme.colors.onPrimary
){
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Row(modifier = Modifier
            .background(color = MaterialTheme.colors.primary)
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
        ){
            Text(
                text = name,
                style = MaterialTheme.typography.h3,
                color = nameColor,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.h3,
                color = valueColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

    }
}





