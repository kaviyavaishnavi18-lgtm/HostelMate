package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val roomNumber: String,
    val email: String,
    val avatarColorHex: String,
    val upiId: String
)

enum class GroupType {
    ROOM,
    MESS,
    TRIP,
    PARTY
}

@Entity(tableName = "expense_groups")
data class ExpenseGroup(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: GroupType,
    val description: String,
    val inviteCode: String,
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "group_members",
    primaryKeys = ["groupId", "studentId"]
)
data class GroupMember(
    val groupId: Long,
    val studentId: Long,
    val role: String = "MEMBER" // ADMIN, MEMBER
)

enum class ExpenseCategory {
    FOOD,
    RENT_UTILITY,
    GROCERY,
    TRIP,
    PARTY,
    OTHER
}

enum class SplitType {
    EQUAL,
    CUSTOM
}

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val title: String,
    val amount: Double,
    val paidByStudentId: Long,
    val category: ExpenseCategory,
    val date: Long = System.currentTimeMillis(),
    val splitType: SplitType = SplitType.EQUAL
)

@Entity(tableName = "expense_splits")
data class ExpenseSplit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expenseId: Long,
    val studentId: Long,
    val amount: Double,
    val isSettled: Boolean = false
)

@Entity(tableName = "mess_bills")
data class MessBill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val monthYear: String, // e.g. "October 2026"
    val totalAmount: Double,
    val totalDays: Int, // e.g. 30 days
    val dailyRate: Double,
    val notes: String = ""
)

@Entity(tableName = "mess_attendances")
data class MessAttendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messBillId: Long,
    val studentId: Long,
    val daysAttended: Int,
    val calculatedAmount: Double,
    val isPaid: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "settlements")
data class Settlement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromStudentId: Long,
    val toStudentId: Long,
    val groupId: Long?,
    val amount: Double,
    val settledDate: Long = System.currentTimeMillis(),
    val paymentMode: String = "UPI" // UPI, CASH
)

// UI and calculation helper data models
data class DebtTransfer(
    val fromStudent: Student,
    val toStudent: Student,
    val amount: Double
)

data class GroupDetail(
    val group: ExpenseGroup,
    val members: List<Student>,
    val totalExpenses: Double,
    val myBalanceInGroup: Double
)

data class StudentBalance(
    val student: Student,
    val totalPaid: Double,
    val totalShare: Double,
    val netBalance: Double // Positive = owed money, Negative = owes money
)
