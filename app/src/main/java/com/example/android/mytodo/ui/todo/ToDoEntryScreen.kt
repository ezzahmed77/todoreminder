package com.example.android.mytodo.ui.todo

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.ToDoScreenAppBar
import com.example.android.mytodo.data.Priority
import com.example.android.mytodo.ui.AppViewModelProvider
import com.example.android.mytodo.ui.navigation.NavDestination
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

object ToDoEntryDestination : NavDestination {
    override val route = "item_entry"
    override val titleRes = R.string.item_entry_title
}

@Composable
fun ToDoEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    viewModel: ToDoEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ToDoScreenAppBar(
                title = stringResource(ToDoEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigationIconClicked = onNavigateUp
            )
        }
    ) { innerPadding ->
        ToDoEntryBody(
            toDoUiState = viewModel.todoUiState,
            onToDoValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch { viewModel.saveToDo() }
                navigateBack()
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ToDoEntryBody(
    toDoUiState: ToDoUiState,
    onToDoValueChange: (ToDoUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        ToDoInputForm(
            toDoUiState = toDoUiState,
            onValueChange = onToDoValueChange,
        )

        Button(
            onClick = onSaveClick,
            enabled = toDoUiState.actionEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.save_action),
                style = MaterialTheme.typography.h3
            )
        }
    }
}

@Composable
fun ToDoInputForm(
    toDoUiState: ToDoUiState,
    modifier: Modifier = Modifier,
    onValueChange: (ToDoUiState) -> Unit = {},
    enabled: Boolean = true
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ToDoBasicInput(
            toDoUiState = toDoUiState,
            onValueChange = onValueChange,
            enabled = enabled
        )
        ToDoPriority(
            toDoUiState = toDoUiState,
            onValueChange = onValueChange
        )

        ToDoDateAndTime(
            toDoUiState = toDoUiState,
            onValueChange = onValueChange,
        )

    }
}

@Composable
fun ToDoBasicInput(
    toDoUiState: ToDoUiState,
    modifier: Modifier = Modifier,
    onValueChange: (ToDoUiState) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        OutlinedTextField(
            value = toDoUiState.title,
            onValueChange = { onValueChange(toDoUiState.copy(title = it)) },
            label = { Text(stringResource(R.string.todo_title_req)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            textStyle = MaterialTheme.typography.h3
        )
        OutlinedTextField(
            value = toDoUiState.description,
            onValueChange = { onValueChange(toDoUiState.copy(description = it)) },
            label = { Text(stringResource(R.string.todo_description)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = false,
            textStyle = MaterialTheme.typography.body1
        )
    }

}

private val priorities : List<Priority> = listOf(
    Priority.High, Priority.Medium, Priority.Low
)

@Composable
fun ToDoPriority(
    modifier: Modifier = Modifier,
    onValueChange: (ToDoUiState) -> Unit = {},
    toDoUiState: ToDoUiState
) {
    // For Priority
    var isExpanded by remember { mutableStateOf(false) }

    val dropDownIcon = if(isExpanded){
        Icons.Filled.KeyboardArrowUp
    }else{
        Icons.Filled.KeyboardArrowDown
    }
    Column(modifier = modifier
        .fillMaxWidth()
        .animateContentSize()) {
        Card(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded },
            shape = MaterialTheme.shapes.medium,
            elevation = 4.dp
        ){
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(id = R.string.priority),
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.weight(.5f)
                )
                Row(
                    modifier = Modifier.weight(1.5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Canvas(modifier = Modifier.size(10.dp), onDraw = {
                        drawCircle(color = toDoUiState.priority.color)
                    })
                    Text(
                        text = toDoUiState.priority.name,
                        style = MaterialTheme.typography.body1
                    )
                }
                Icon(
                    imageVector = dropDownIcon,
                    contentDescription = null ,
                )

            }
        }


        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.fillMaxWidth(.5f)
        ) {
            priorities.forEach {priority->
                DropdownMenuItem(onClick = {
                    onValueChange(toDoUiState.copy(priority = priority))
                    isExpanded = false },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Canvas(modifier = Modifier.size(10.dp), onDraw = {
                            drawCircle(color = priority.color)
                        })
                        Text(
                            text = priority.name,
                            style = MaterialTheme.typography.h3
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ToDoDateAndTime(
    modifier: Modifier = Modifier,
    toDoUiState: ToDoUiState,
    onValueChange: (ToDoUiState) -> Unit,
) {
    // For Date & Time Dialogs
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Date & Time",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.h3
            )
            Switch(
                checked = toDoUiState.hasDateAndTime,
                onCheckedChange = {
                    onValueChange(toDoUiState.copy(hasDateAndTime = it))
                },
                modifier = Modifier.weight(1f)
            )
        }

        if(toDoUiState.hasDateAndTime){
            ToDoEntryDateTimeItem(
                onClick = {dateDialogState.show()},
                name = "Date:",
                value = toDoUiState.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            )

            ToDoEntryDateTimeItem(
                onClick = {timeDialogState.show()},
                name = "Time:",
                value = toDoUiState.time.format(DateTimeFormatter.ofPattern("hh:mm"))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Add Reminder",
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = toDoUiState.hasReminder,
                    onCheckedChange = {
                        onValueChange(toDoUiState.copy(hasReminder = it))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Date And Time Dialogs
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(
                    text = "Ok",
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
                negativeButton(
                    text = "Cancel",
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
        ) {
            datepicker(
                initialDate = toDoUiState.date,
                title = "Pick a date",

            ) {
                onValueChange(toDoUiState.copy(date = it))
            }
        }

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(
                    text = "Ok",
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
                negativeButton(
                    text = "Cancel",
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
        ) {
            timepicker(
                initialTime = toDoUiState.time,
                title = "Pick a time"
            ) {
                onValueChange(toDoUiState.copy(time = it))
            }
        }

    }

}

@Composable
fun ToDoEntryDateTimeItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    name: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ){
            Text(
                text = name,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}