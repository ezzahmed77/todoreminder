package com.example.android.mytodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [ToDo::class], version = 1, exportSchema = false)
abstract class ToDoDatabase(): RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

    companion object{
        @Volatile
        private var instance : ToDoDatabase? = null

        fun getDatabase(context: Context) : ToDoDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, ToDoDatabase::class.java, "todo_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { instance = it }
            }
        }
    }

}
