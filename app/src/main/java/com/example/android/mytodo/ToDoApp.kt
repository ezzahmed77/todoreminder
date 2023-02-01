package com.example.android.mytodo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.android.mytodo.ui.navigation.ToDoNavHost
import com.example.android.mytodo.ui.todo.FilterType


/**
 * Top level composable that represents screens for the application.
 */
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
fun ToDoScreenAppBar(
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
        modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colors.primary),
        navigationIcon = {
            IconButton(onClick = onNavigationIconClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button)
                )
            }
        }
    )
}
/**
 * App bar to display title and conditionally display the back navigation.
 */
@Composable
fun ToDoFilterAppBar(
    filterType: FilterType,
    modifier: Modifier = Modifier,
    onNavigationIconClicked: () -> Unit = {},
    actions: @Composable RowScope.()->Unit = { },
) {
    val icon = if(filterType == FilterType.ALL) Icons.Filled.Menu else Icons.Filled.ArrowBack
    val contentDescription = if(filterType == FilterType.ALL) {
        stringResource(id = R.string.dropdown_menu)
    }else{
        stringResource(id = R.string.back_button)
    }
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = filterType.title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 18.sp,
            )
        },
        modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colors.primary),
        navigationIcon = {
            IconButton(onClick = onNavigationIconClicked) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription
                )
            }
        },
        actions = actions
    )
}