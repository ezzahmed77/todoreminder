package com.example.android.mytodo.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.ToDoFilterAppBar
import com.example.android.mytodo.data.model.Todo
import com.example.android.mytodo.ui.AppViewModelProvider
import com.example.android.mytodo.ui.navigation.NavDestination
import com.example.android.mytodo.ui.theme.MyToDoTheme
import com.example.android.mytodo.ui.todo.FilterType
import com.example.android.mytodo.ui.todo.TodoFilteredList
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class Sort {
    PRIORITY, TITLE
}

object HomeDestination : NavDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.app_name
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToToDoDetail: (Int) -> Unit = {},
    navigateToToDoEntry: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Getting homeUiState
    val homeUiState by viewModel.homeUiState.collectAsState()
    val sort = viewModel.sort
    val filter = viewModel.filter
    val list = getSortedList(getListFromState(homeUiState.todos, filter), sort)

    // States for Scaffold and viewModel
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    // States for menu actions
    var showDetails by remember { mutableStateOf(true) }
    var showCompleted by remember { mutableStateOf(false) }

    // Determining actions of swiping based on the filter
    val swipeLeft: (Todo) -> Unit = {
        if (filter != FilterType.TRASH) {
            scope.launch {
                viewModel.updateTodo(it.copy(isTrashed = true))
            }
        } else {
            scope.launch {
                viewModel.deleteTodo(it)
            }
        }
    }
    val swipeRight: (Todo) -> Unit = {
        if (filter == FilterType.TRASH) {
            scope.launch {
                viewModel.updateTodo(it.copy(isTrashed = false))
            }
        }
    }

    val closeNavDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ToDoFilterAppBar(
                filterType = filter,
                onNavigationIconClicked = { scope.launch { scaffoldState.drawerState.open() } },
                onShowCompletedChange = { showCompleted = !showCompleted },
                onShowDetailsChange = { showDetails = !showDetails },
                onSortUpdate = { viewModel.updateSort(it) },
                onDeleteAllTodosInTrash = { scope.launch { viewModel.deleteAllTodosInTrash() } }
            )

        },
        floatingActionButton = {
            if (filter != FilterType.TRASH) {
                FloatingActionButton(
                    onClick = navigateToToDoEntry,
                    modifier = Modifier.navigationBarsPadding(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.item_entry_title),
                    )
                }
            }
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                filter = filter,
                closeNavDrawer = { closeNavDrawer() },
                viewModel = viewModel
            )
        }

    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filter != FilterType.COMPLETED) {
                TodoFilteredList(
                    modifier = Modifier.padding(innerPadding),
                    filter = filter,
                    onTodoClick = navigateToToDoDetail,
                    list = list.filter { !it.isCompleted },
                    onCheckedChange = {
                        scope.launch {
                            viewModel.updateTodo(it.copy(isCompleted = !it.isCompleted))
                        }
                    },
                    onSwipeLeft = swipeLeft,
                    onSwipeRight = swipeRight,
                    showDetails = showDetails
                )
            }

            if (showCompleted || filter == FilterType.COMPLETED) {

                var isExpanded by remember { mutableStateOf(false) }
                val icon = if (isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val completedTodosCount = list.filter { it.isCompleted }.size
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = 4.dp
                    ) {

                        Row(
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.h3,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = completedTodosCount.toString(),
                                style = MaterialTheme.typography.h3,
                                modifier = Modifier.padding(end = 32.dp)
                            )

                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        }

                    }

                    if (isExpanded) {
                        TodoFilteredList(
                            modifier = Modifier.padding(innerPadding),
                            filter = filter,
                            onTodoClick = navigateToToDoDetail,
                            list = list.filter { it.isCompleted },
                            onCheckedChange = {
                                scope.launch {
                                    viewModel.updateTodo(it.copy(isCompleted = !it.isCompleted))
                                }
                            },
                            onSwipeLeft = swipeLeft,
                            onSwipeRight = swipeRight,
                            showDetails = showDetails
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun DrawerContent(
    filter: FilterType,
    closeNavDrawer: () -> Unit,
    viewModel: HomeViewModel
) {
    val drawerMenuItems = listOf(
        DrawerMenuItem(
            imageVector = Icons.Default.AccountBox,
            text = stringResource(id = R.string.all),
            selected = filter == FilterType.ALL,
            onItemClick = {
                viewModel.updateFilter(FilterType.ALL)
                closeNavDrawer()
            },
        ),
        DrawerMenuItem(
            imageVector = Icons.Default.DateRange,
            text = stringResource(id = R.string.today),
            onItemClick = {
                viewModel.updateFilter(FilterType.TODAY)
                closeNavDrawer()
            },
            selected = filter == FilterType.TODAY
        ),
        DrawerMenuItem(
            imageVector = Icons.Default.DateRange,
            text = stringResource(id = R.string.tomorrow),
            onItemClick = {
                viewModel.updateFilter(FilterType.TOMORROW)
                closeNavDrawer()
            },
            selected = filter == FilterType.TOMORROW
        ),
        DrawerMenuItem(
            imageVector = Icons.Default.DateRange,
            text = stringResource(id = R.string.next_seven_days),
            onItemClick = {
                viewModel.updateFilter(FilterType.NEXT_SEVEN_DAYS)
                closeNavDrawer()
            },
            selected = filter == FilterType.NEXT_SEVEN_DAYS
        ),
        DrawerMenuItem(
            imageVector = Icons.Default.DateRange,
            text = stringResource(id = R.string.completed),
            onItemClick = {
                viewModel.updateFilter(FilterType.COMPLETED)
                closeNavDrawer()
            },
            selected = filter == FilterType.COMPLETED
        ),
        DrawerMenuItem(
            imageVector = Icons.Default.Delete,
            text = stringResource(id = R.string.trash),
            onItemClick = {
                viewModel.updateFilter(FilterType.TRASH)
                closeNavDrawer()
            },
            selected = filter == FilterType.TRASH
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(drawerMenuItems) { menuItem ->
            menuItem
        }
    }
}


@Composable
fun TodoCheckedMenuItem(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    checked: Boolean,
    onCheckChange: () -> Unit

) {
    DropdownMenuItem(onClick = onCheckChange) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(1f)
            )
            RadioButton(selected = checked, onClick = onCheckChange)

        }
    }
}


@Composable
fun TodoMenuItem(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(onClick = onClick) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    imageVector: ImageVector,
    text: String,
    selected: Boolean = false,
    onItemClick: () -> Unit
) {
    val colors = MaterialTheme.colors

    val backgroundColor = if (selected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        colors.primary
    } else {
        colors.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onItemClick() }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = text,
            tint = contentColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.h6.copy(
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            ),
            color = contentColor
        )
    }
}


private fun getListFromState(list: List<Todo>, filter: FilterType): List<Todo> {
    return when (filter) {
        FilterType.ALL -> list.filter { !it.isTrashed }

        FilterType.TODAY -> list.filter {
            it.hasDateAndTime
                    && it.date.dayOfMonth == LocalDate.now().dayOfMonth
                    && !it.isTrashed
        }

        FilterType.TOMORROW -> list.filter {
            (
                it.hasDateAndTime
                    && it.date.dayOfMonth == (LocalDate.now().dayOfMonth + 1)
                    && !it.isTrashed
                )
        }

        FilterType.NEXT_SEVEN_DAYS -> list.filter {
            it.hasDateAndTime
                    && (it.date.dayOfMonth >= LocalDate.now().dayOfMonth + 1 && it.date.dayOfMonth <= LocalDate.now().dayOfMonth + 7)
                    && !it.isTrashed
        }

        FilterType.COMPLETED -> list.filter { it.isCompleted }

        else -> list.filter { it.isTrashed }
    }
}

private fun getSortedList(list: List<Todo>, type: Sort): List<Todo> {
    return when (type) {
        Sort.TITLE -> list.sortedBy { it.title }
        else -> list.sortedBy { it.priority.id }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun HomeScreenPreview() {
    MyToDoTheme() {
        HomeScreen()
    }
}




