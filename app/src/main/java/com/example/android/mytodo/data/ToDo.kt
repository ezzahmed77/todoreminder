package com.example.android.mytodo.data

import androidx.compose.ui.graphics.Color
import androidx.room.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "todo")
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "priority")
    @TypeConverters(PriorityConverter::class)
    val priority: Priority,
    @ColumnInfo(name = "hasDateAndTime")
    val hasDateAndTime: Boolean = false,
    @ColumnInfo(name = "data")
    @TypeConverters(DateAndTimeConverter::class)
    val date: LocalDate = LocalDate.now(),
    @TypeConverters(DateAndTimeConverter::class)
    @ColumnInfo(name = "time")
    val time: LocalTime = LocalTime.now(),
    @ColumnInfo(name = "hasReminder")
    val hasReminder: Boolean = false,
    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "isTrashed")
    val isTrashed: Boolean = false
)


sealed class Priority(
    val name: String,
    val color: Color,
    val id: Int
){
    object High: Priority("High", Color.Red, 1)
    object Medium: Priority("Medium", Color(0xFFD1DC1A), 2)
    object Low: Priority("Low", Color.Green, 3)
    object NotSelected: Priority("Not selected", Color.Black, 4)
}

class PriorityConverter(){
    @TypeConverter
    fun priorityToInt(priority: Priority): Int{
        return when(priority){
            Priority.High -> 1
            Priority.Medium -> 2
            Priority.Low -> 3
            else-> 4
        }
    }

    @TypeConverter
    fun intToPriority(num: Int): Priority{
        return when(num){
            1-> Priority.High
            2-> Priority.Medium
            3-> Priority.Low
            else-> Priority.NotSelected
        }
    }
}

class DateAndTimeConverter(){
    @TypeConverter
    fun localDateToString(date: LocalDate): String{
        return date.format(DateTimeFormatter.ISO_DATE)
    }
    @TypeConverter
    fun stringToLocalDate(dateString: String): LocalDate{
        return LocalDate.parse(dateString)
    }
    @TypeConverter
    fun localTimeToString(time: LocalTime) : String{
        return time.format(DateTimeFormatter.ISO_TIME)
    }
    @TypeConverter
    fun stringToLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString)
    }
}