package com.example.android.mytodo.ui.todo

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.ToDoFilterAppBar
import com.example.android.mytodo.ToDoScreenAppBar
import com.example.android.mytodo.ui.AppViewModelProvider
import com.example.android.mytodo.ui.navigation.NavDestination
import kotlinx.coroutines.launch

object ToDoEditDestination : NavDestination {
    override val route = "item_edit"
    override val titleRes = R.string.edit_todo_title
    const val toDoIdArg = "todoId"
    val routeWithArgs = "$route/{$toDoIdArg}"
}


@Composable
fun ToDoEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ToDoEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ToDoScreenAppBar(
                title = stringResource(ToDoEditDestination.titleRes),
                canNavigateBack = true,
                onNavigationIconClicked = onNavigateUp
            )
        }
    ) { innerPadding ->
        ToDoEntryBody(
            toDoUiState = viewModel.toDoUiState,
            onToDoValueChange = viewModel::updateUiState,
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