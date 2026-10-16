package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.HostelMateDao
import com.example.data.models.Expense
import com.example.data.models.ExpenseGroup
import com.example.data.models.ExpenseSplit
import com.example.data.models.GroupMember
import com.example.data.models.MessAttendance
import com.example.data.models.MessBill
import com.example.data.models.Settlement
import com.example.data.models.Student

@Database(
    entities = [
        Student::class,
        ExpenseGroup::class,
        GroupMember::class,
        Expense::class,
        ExpenseSplit::class,
        MessBill::class,
        MessAttendance::class,
        Settlement::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hostelMateDao(): HostelMateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hostelmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
