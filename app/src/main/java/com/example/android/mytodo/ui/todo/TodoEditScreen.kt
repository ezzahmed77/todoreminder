package com.example.android.mytodo.ui.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.TodoScreenAppBar
import com.example.android.mytodo.ui.AppViewModelProvider
import com.example.android.mytodo.ui.navigation.NavDestination
import kotlinx.coroutines.launch

object ToDoEditDestination : NavDestination {
    override val route = "item_edit"
    override val titleRes = R.string.edit_todo_title
    const val toDoIdArg = "todoId"
    val routeWithArgs = "$route/{$toDoIdArg}"
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TodoEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodoEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TodoScreenAppBar(
                title = stringResource(ToDoEditDestination.titleRes),
                canNavigateBack = true,
                onNavigationIconClicked = onNavigateUp
            )
        }
    ) { innerPadding ->
        TodoEntryBody(
            todoUiState = viewModel.todoUiState,
            onTodoValueChange = viewModel::updateUiState,
            onSaveClick = {
                scope.launch {
                    viewModel.updateToDo()
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}