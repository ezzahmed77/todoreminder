package com.example.android.mytodo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.android.mytodo.ui.home.HomeScreen
import com.example.android.mytodo.ui.home.HomeDestination
import com.example.android.mytodo.ui.todo.*

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ToDoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToToDoDetail = {
                    navController.navigate("${ToDoDetailsDestination.route}/${it}")
                },
                navigateToToDoEntry = { navController.navigate(ToDoEntryDestination.route) })
        }
        composable(route = ToDoEntryDestination.route) {
            TodoEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = ToDoDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ToDoDetailsDestination.todoIdArg) {
                type = NavType.IntType
            })
        ) {
            TodoDetailScreen(
                navigateToToDoEdit = {
                    navController.navigate("${ToDoEditDestination.route}/$it")
                },
                navigateBack = { navController.navigateUp() })
        }
        composable(
            route = ToDoEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ToDoEditDestination.toDoIdArg) {
                type = NavType.IntType
            })
        ) {
            TodoEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}