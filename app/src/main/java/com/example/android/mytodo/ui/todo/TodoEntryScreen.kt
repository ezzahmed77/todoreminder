package com.example.android.mytodo.ui.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.TodoScreenAppBar
import com.example.android.mytodo.data.model.Priority
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

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TodoEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    viewModel: TodoEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TodoScreenAppBar(
                title = stringResource(ToDoEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigationIconClicked = onNavigateUp
            )
        }
    ) { innerPadding ->
        TodoEntryBody(
            todoUiState = viewModel.todoUiState,
            onTodoValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch { viewModel.saveToDo() }
                navigateBack()
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TodoEntryBody(
    todoUiState: TodoUiState,
    onTodoValueChange: (TodoUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TodoInputForm(
            toDoUiState = todoUiState,
            onValueChange = onTodoValueChange,
        )

        Button(
            onClick = onSaveClick,
            enabled = todoUiState.actionEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.save_action),
                style = MaterialTheme.typography.h6,
            )
        }
    }
}

@Composable
fun TodoInputForm(
    toDoUiState: TodoUiState,
    modifier: Modifier = Modifier,
    onValueChange: (TodoUiState) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoBasicInput(
            toDoUiState = toDoUiState,
            onValueChange = onValueChange,
            enabled = enabled
        )
        TodoPriority(
            toDoUiState = toDoUiState,
            onValueChange = onValueChange
        )
        TodoDateAndTime(
            toDoUiState = toDoUiState,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun TodoBasicInput(
    toDoUiState: TodoUiState,
    modifier: Modifier = Modifier,
    onValueChange: (TodoUiState) -> Unit = {},
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
            textStyle = MaterialTheme.typography.h5,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                backgroundColor = MaterialTheme.colors.surface
            )
        )
        OutlinedTextField(
            value = toDoUiState.description,
            onValueChange = { onValueChange(toDoUiState.copy(description = it)) },
            label = { Text(stringResource(R.string.todo_description)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = false,
            textStyle = MaterialTheme.typography.body1,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                backgroundColor = MaterialTheme.colors.surface
            )
        )
    }
}

private val priorities : List<Priority> = listOf(
    Priority.High, Priority.Medium, Priority.Low
)


@Composable
fun TodoPriority(
    modifier: Modifier = Modifier,
    onValueChange: (TodoUiState) -> Unit = {},
    toDoUiState: TodoUiState
) {
    var isExpanded by remember { mutableStateOf(false) }

    val dropDownIcon = if(isExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(toDoUiState.priority.color),
                onDraw = {}
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.priority),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = toDoUiState.priority.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = dropDownIcon,
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(onClick = {
                    onValueChange(toDoUiState.copy(priority = priority))
                    isExpanded = false
                }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(priority.color),
                            onDraw = {}
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = priority.name,
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoDateAndTime(
    modifier: Modifier = Modifier,
    toDoUiState: TodoUiState,
    onValueChange: (TodoUiState) -> Unit,
) {
    // For Date & Time Dialogs
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Default.DateRange,
                modifier = Modifier.padding(end = 8.dp),
                contentDescription = null)
            Text(
                text = stringResource(id = R.string.date_and_time),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
            )
            Switch(
                checked = toDoUiState.hasDateAndTime,
                onCheckedChange = {
                    onValueChange(toDoUiState.copy(hasDateAndTime = it))
                },
            )
        }

        if(toDoUiState.hasDateAndTime){
            TodoEntryDateTimeItem(
                onClick = {dateDialogState.show()},
                name = stringResource(id = R.string.date),
                value = toDoUiState.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            )

            TodoEntryDateTimeItem(
                onClick = {timeDialogState.show()},
                name = stringResource(id = R.string.time),
                value = toDoUiState.time.format(DateTimeFormatter.ofPattern("hh:mm"))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(id = R.string.add_reminder),
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
                    text = stringResource(id = R.string.ok),
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.primary
                    )
                )
                negativeButton(
                    text = stringResource(id = R.string.cancel),
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.primary
                    )
                )
            }
        ) {
            datepicker(
                initialDate = toDoUiState.date,
                title = stringResource(id = R.string.pick_date),

            ) {
                onValueChange(toDoUiState.copy(date = it))
            }
        }

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(
                    text = stringResource(id = R.string.ok),
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.primary
                    )
                )
                negativeButton(
                    text = stringResource(id = R.string.cancel),
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.primary
                    )
                )
            }
        ) {
            timepicker(
                initialTime = toDoUiState.time,
                title = stringResource(id = R.string.pick_time)
            ) {
                onValueChange(toDoUiState.copy(time = it))
            }
        }

    }

}

@Composable
fun TodoEntryDateTimeItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    name: String,
    value: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ){
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
        ){
            Text(
                text = name,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}