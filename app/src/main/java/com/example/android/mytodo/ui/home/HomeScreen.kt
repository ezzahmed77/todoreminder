package com.example.android.mytodo.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.mytodo.R
import com.example.android.mytodo.ToDoFilterAppBar
import com.example.android.mytodo.ui.AppViewModelProvider
import com.example.android.mytodo.ui.navigation.NavDestination
import com.example.android.mytodo.ui.theme.MyToDoTheme
import com.example.android.mytodo.ui.todo.FilterType
import com.example.android.mytodo.ui.todo.ToDoFilteredList
import com.example.android.mytodo.ui.todo.ToDoUiState
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class Sort{
    PRIORITY, TITLE
}

object HomeDestination: NavDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.app_name
}

private fun getSortedList(list: List<ToDoUiState>, type: Sort): List<ToDoUiState> {
    return when(type){
        Sort.TITLE -> list.sortedBy { it.title }
        else -> list.sortedBy { it.priority.id }
    }
}
@Preview
@Composable
fun HomeScreenPreview(){
    MyToDoTheme() {
        HomeScreen()
    }
}
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToToDoDetail: (Int) -> Unit = {},
    navigateToToDoEntry: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

    // Getting homeUiState
    val homeUiState  by viewModel.homeUiState.collectAsState()
    val sort = viewModel.sort
    val listFilterType = viewModel.filter
    val list = getSortedList(getListFromState(homeUiState.todosUiState, listFilterType), sort)


    // States for Scaffold and viewModel
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    // States for menu actions
    var showMenu by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(true) }
    var showCompleted by remember { mutableStateOf(false) }

    // Determining actions of swiping based on the filter
    val swipeLeft: (ToDoUiState)-> Unit = {
        if(listFilterType != FilterType.TRASH){
            scope.launch {
                viewModel.updateToDo(it.copy(isTrashed = true))
            }
        }else{
            scope.launch {
                viewModel.deleteToDo(it)
            }
        }
    }
    val swipeRight: (ToDoUiState) -> Unit = {
        if(listFilterType == FilterType.TRASH){
            scope.launch {
                viewModel.updateToDo(it.copy(isTrashed = false))
            }
        }
    }

    val closeNavDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ToDoFilterAppBar(
                filterType = listFilterType,
                onNavigationIconClicked = { scope.launch { scaffoldState.drawerState.open() } },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.dropdown_menu)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false}
                    ) {
                        if(listFilterType != FilterType.TRASH) {
                            ToDoCheckedMenuItem(
                                icon = R.drawable.details_icon,
                                text = stringResource(id = R.string.show_details),
                                checked = showDetails,
                                onCheckChange = { showDetails = !showDetails}
                            )
                            if(listFilterType  != FilterType.COMPLETED){
                                ToDoCheckedMenuItem(
                                    icon = R.drawable.completed_checked_box,
                                    text = stringResource(id = R.string.show_completed),
                                    checked = showCompleted,
                                    onCheckChange = { showCompleted = !showCompleted }
                                )
                            }

                            ToDoMenuItem(
                                icon = R.drawable.sort_icon,
                                text = stringResource(id = R.string.sort_by_title),
                                onClick = { viewModel.updateSort(Sort.TITLE) }
                            )
                            ToDoMenuItem(
                                icon = R.drawable.sort_icon,
                                text = stringResource(id = R.string.sort_by_priority),
                                onClick = { viewModel.updateSort(Sort.PRIORITY)}
                            )
                        }
                        else {
                            ToDoMenuItem(
                                icon = R.drawable.empty_trash_icon,
                                text = stringResource(id = R.string.empty_trash),
                                onClick = { scope.launch { viewModel.deleteAllToDosInTrash() } }
                            )
                        }
                    }

                }
            )
        },
        floatingActionButton = {
            if(listFilterType != FilterType.TRASH){
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
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                DrawerMenuItem(
                    imageVector = Icons.Default.AccountBox,
                    text = stringResource(id = R.string.all),
                    onItemClick = {
                        viewModel.updateFilter(FilterType.ALL)
                        closeNavDrawer()
                    },
                    tint = Color.Blue
                )
                DrawerMenuItem(
                    imageVector = Icons.Default.DateRange,
                    text = stringResource(id = R.string.today),
                    onItemClick = {
                        viewModel.updateFilter(FilterType.TODAY)
                        closeNavDrawer()
                    },
                    tint = Color.Magenta
                )
                DrawerMenuItem(
                    imageVector = Icons.Default.DateRange,
                    text = stringResource(id = R.string.tomorrow),
                    onItemClick = {
                        viewModel.updateFilter(FilterType.TOMORROW)
                        closeNavDrawer()
                    },
                    tint = Color.Yellow
                )
                DrawerMenuItem(
                    imageVector = Icons.Default.DateRange,
                    text = stringResource(id = R.string.next_seven_days),
                    onItemClick = {
                        viewModel.updateFilter(FilterType.NEXT_SEVEN_DAYS)
                        closeNavDrawer()
                    },
                    tint = Color.Red
                )

                DrawerMenuItem(
                    imageVector = Icons.Default.DateRange,
                    text = stringResource(id = R.string.completed),
                    onItemClick = {
                        viewModel.updateFilter(FilterType.COMPLETED)
                        closeNavDrawer()
                    },
                    tint = Color.Green
                )
                DrawerMenuItem(
                    imageVector = Icons.Default.DateRange,
                    text = stringResource(id = R.string.trash),
                    onItemClick = {
                        viewModel.updateFilter(FilterType.TRASH)
                        closeNavDrawer()
                    },
                    tint = Color.Blue
                )
            }
        }

    ) { innerPadding->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            if(listFilterType != FilterType.COMPLETED){
                ToDoFilteredList(
                    modifier = Modifier.padding(innerPadding),
                    filter = listFilterType,
                    onToDoClick = navigateToToDoDetail,
                    list = list.filter { !it.isCompleted },
                    onCheckedChange = {
                        scope.launch {
                            viewModel.updateToDo(it.copy(isCompleted = !it.isCompleted))
                        }
                    },
                    onSwipeLeft = swipeLeft,
                    onSwipeRight = swipeRight,
                    showDetails = showDetails
                )
            }

            if(showCompleted || listFilterType == FilterType.COMPLETED){

                var isExpanded by remember { mutableStateOf(false)}
                val icon = if(isExpanded){
                    Icons.Default.KeyboardArrowUp
                }else{
                    Icons.Default.KeyboardArrowDown
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = 4.dp
                    ){
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { isExpanded = !isExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.h3,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = list.filter { it.isCompleted }.size.toString(),
                                style = MaterialTheme.typography.h3,
                                modifier = Modifier.padding(end = 32.dp)
                            )
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        }
                    }
                    if(isExpanded){
                        ToDoFilteredList(
                            modifier = Modifier.padding(innerPadding),
                            filter = listFilterType,
                            onToDoClick = navigateToToDoDetail,
                            list = list.filter { it.isCompleted },
                            onCheckedChange = {
                                scope.launch {
                                    viewModel.updateToDo(it.copy(isCompleted = !it.isCompleted))
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
fun ToDoCheckedMenuItem(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    checked: Boolean,
    onCheckChange: () -> Unit

){
    DropdownMenuItem(onClick = onCheckChange) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray
            )
            Text(
                text = text,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.weight(1f)
            )
            RadioButton(selected = checked, onClick = onCheckChange )
        }
    }
}

@Composable
fun ToDoMenuItem(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    onClick: () -> Unit
){
    DropdownMenuItem(onClick = onClick) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray
            )
            Text(
                text = text,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    imageVector: ImageVector,
    text: String,
    tint: Color = Color.Black,
    onItemClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = text,
            tint = tint
        )
        Text(
            text = text,
            style = MaterialTheme.typography.h3
        )
    }
}


private fun getListFromState(list: List<ToDoUiState>, filter: FilterType): List<ToDoUiState> {
    return when(filter){
        FilterType.ALL -> list.filter{!it.isTrashed}

        FilterType.TODAY -> list.filter{
            it.hasDateAndTime
                    && it.date.dayOfMonth == LocalDate.now().dayOfMonth
                    && !it.isTrashed
        }

        FilterType.TOMORROW -> list.filter{
            (
                    it.hasDateAndTime
                            && it.date.dayOfMonth == (LocalDate.now().dayOfMonth + 1 )
                            && !it.isTrashed
                    )

        }

        FilterType.NEXT_SEVEN_DAYS -> list.filter{
            it.hasDateAndTime
                    && (it.date.dayOfMonth >= LocalDate.now().dayOfMonth + 1 && it.date.dayOfMonth <= LocalDate.now().dayOfMonth + 7)
                    && !it.isTrashed
        }

        FilterType.COMPLETED -> list.filter{ it.isCompleted }

        else-> list.filter { it.isTrashed }
    }
}


