package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.models.ExpenseCategory
import com.example.data.models.GroupType
import com.example.data.models.SplitType

class Converters {
    @TypeConverter
    fun fromGroupType(value: GroupType): String = value.name

    @TypeConverter
    fun toGroupType(value: String): GroupType = try {
        GroupType.valueOf(value)
    } catch (e: Exception) {
        GroupType.ROOM
    }

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name

    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = try {
        ExpenseCategory.valueOf(value)
    } catch (e: Exception) {
        ExpenseCategory.OTHER
    }

    @TypeConverter
    fun fromSplitType(value: SplitType): String = value.name

    @TypeConverter
    fun toSplitType(value: String): SplitType = try {
        SplitType.valueOf(value)
    } catch (e: Exception) {
        SplitType.EQUAL
    }
}
