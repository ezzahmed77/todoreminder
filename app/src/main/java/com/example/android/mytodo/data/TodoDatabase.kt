package com.example.android.mytodo.data

import android.content.Context
import androidx.room.*
import com.example.android.mytodo.data.model.DateAndTimeConverter
import com.example.android.mytodo.data.model.PriorityConverter
import com.example.android.mytodo.data.model.Todo


@Database(entities = [Todo::class], version = 1, exportSchema = false)
@TypeConverters(PriorityConverter::class, DateAndTimeConverter::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object{
        @Volatile
        private var instance : TodoDatabase? = null

        fun getDatabase(context: Context) : TodoDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { instance = it }
            }
        }
    }

}
