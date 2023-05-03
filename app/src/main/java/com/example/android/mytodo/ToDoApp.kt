package com.example.android.mytodo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.android.mytodo.ui.home.Sort
import com.example.android.mytodo.ui.home.TodoCheckedMenuItem
import com.example.android.mytodo.ui.home.TodoMenuItem
import com.example.android.mytodo.ui.navigation.ToDoNavHost
import com.example.android.mytodo.ui.todo.FilterType


/**
 * Top level composable that represents screens for the application.
 */
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ToDoApp(navController: NavHostController = rememberNavController()) {
    ToDoNavHost(
        navController = navController,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    )
}

@Composable
fun TodoScreenAppBar(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack:Boolean = true,
    onNavigationIconClicked: () -> Unit
){
    TopAppBar(
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 18.sp,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary),
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = onNavigationIconClicked) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }

        }
    )
}
/**
 *
 * App bar to display title and conditionally display the back navigation.
 */

@Composable
fun ToDoFilterAppBar(
    filterType: FilterType,
    onNavigationIconClicked: () -> Unit,
    onShowCompletedChange: () -> Unit,
    onShowDetailsChange: () -> Unit,
    onSortUpdate: (Sort) -> Unit,
    onDeleteAllTodosInTrash: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = filterType.title)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClicked) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = androidx.compose.ui.R.string.navigation_menu)
                )
            }
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(id = R.string.dropdown_menu)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (filterType != FilterType.TRASH) {
                    TodoCheckedMenuItem(
                        icon = R.drawable.details_icon,
                        text = stringResource(id = R.string.show_details),
                        checked = false,
                        onCheckChange = onShowDetailsChange
                    )
                    if (filterType != FilterType.COMPLETED) {
                        TodoCheckedMenuItem(
                            icon = R.drawable.completed_checked_box,
                            text = stringResource(id = R.string.show_completed),
                            checked = false,
                            onCheckChange = onShowCompletedChange
                        )
                    }

                    TodoMenuItem(
                        icon = R.drawable.sort_icon,
                        text = stringResource(id = R.string.sort_by_title),
                        onClick = { onSortUpdate(Sort.TITLE) }
                    )
                    TodoMenuItem(
                        icon = R.drawable.sort_icon,
                        text = stringResource(id = R.string.sort_by_priority),
                        onClick = { onSortUpdate(Sort.PRIORITY) }
                    )
                } else {
                    TodoMenuItem(
                        icon = R.drawable.empty_trash_icon,
                        text = stringResource(id = R.string.empty_trash),
                        onClick = onDeleteAllTodosInTrash
                    )
                }
            }

        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 0.dp,
    )
}


