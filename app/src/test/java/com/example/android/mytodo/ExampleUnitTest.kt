package com.example.android.mytodo

import com.example.android.mytodo.ui.todo.FilterType
import com.example.android.mytodo.ui.todo.TodoUiState
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun getToDosBasedOnFilter(){
        val listOfToday = getListFromState(listOfTodoUiStates, FilterType.NEXT_SEVEN_DAYS)
        listOfToday.forEach {
            println(it.title)
        }
        assertEquals(4, listOfToday.size)
    }
    private fun getListFromState(list: List<TodoUiState>, filter: FilterType): List<TodoUiState> {
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


    private val listOfTodoUiStates = listOf(
        TodoUiState(
            id = 0,
            title = "Today todoUi",
            hasDateAndTime = true,
            date = LocalDate.now()
        ),
        TodoUiState(
            id = 1,
            title = "Tomorrow todoUi",
            hasDateAndTime = true,
            date = LocalDate.now().plusDays(1)
        ),
        TodoUiState(
            id = 2,
            title = "Next Week todoUi",
            hasDateAndTime = true,
            date = LocalDate.now().plusDays(2)
        ),
        TodoUiState(
            id = 3,
            title = "Next Week todoUi",
            hasDateAndTime = true,
            date = LocalDate.now().plusDays(3)
        ),
        TodoUiState(
            id = 4,
            title = "Next Week todoUi",
            hasDateAndTime = true,
            date = LocalDate.now().plusDays(4)
        ),

        )
}