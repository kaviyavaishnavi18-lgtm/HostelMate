package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.Expense
import com.example.data.models.ExpenseGroup
import com.example.data.models.ExpenseSplit
import com.example.data.models.GroupMember
import com.example.data.models.MessAttendance
import com.example.data.models.MessBill
import com.example.data.models.Settlement
import com.example.data.models.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface HostelMateDao {

    // Students
    @Query("SELECT * FROM students ORDER BY id ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Long): Flow<Student?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    // Groups
    @Query("SELECT * FROM expense_groups ORDER BY createdDate DESC")
    fun getAllGroups(): Flow<List<ExpenseGroup>>

    @Query("SELECT * FROM expense_groups WHERE id = :id")
    fun getGroupById(id: Long): Flow<ExpenseGroup?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: ExpenseGroup): Long

    @Delete
    suspend fun deleteGroup(group: ExpenseGroup)

    // Group Members
    @Query("""
        SELECT s.* FROM students s
        INNER JOIN group_members gm ON s.id = gm.studentId
        WHERE gm.groupId = :groupId
    """)
    fun getGroupMembers(groupId: Long): Flow<List<Student>>

    @Query("SELECT * FROM group_members")
    fun getAllGroupMembers(): Flow<List<GroupMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(groupMember: GroupMember)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMembers(groupMembers: List<GroupMember>)

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND studentId = :studentId")
    suspend fun removeGroupMember(groupId: Long, studentId: Long)

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY date DESC")
    fun getExpensesByGroup(groupId: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // Splits
    @Query("SELECT * FROM expense_splits")
    fun getAllSplits(): Flow<List<ExpenseSplit>>

    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    fun getSplitsForExpense(expenseId: Long): Flow<List<ExpenseSplit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplits(splits: List<ExpenseSplit>)

    @Query("UPDATE expense_splits SET isSettled = :isSettled WHERE id = :splitId")
    suspend fun updateSplitSettlement(splitId: Long, isSettled: Boolean)

    // Mess Bills
    @Query("SELECT * FROM mess_bills ORDER BY id DESC")
    fun getAllMessBills(): Flow<List<MessBill>>

    @Query("SELECT * FROM mess_bills WHERE id = :id")
    fun getMessBillById(id: Long): Flow<MessBill?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessBill(bill: MessBill): Long

    @Delete
    suspend fun deleteMessBill(bill: MessBill)

    // Mess Attendances
    @Query("SELECT * FROM mess_attendances WHERE messBillId = :messBillId")
    fun getMessAttendances(messBillId: Long): Flow<List<MessAttendance>>

    @Query("SELECT * FROM mess_attendances")
    fun getAllMessAttendances(): Flow<List<MessAttendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessAttendances(attendances: List<MessAttendance>)

    @Query("UPDATE mess_attendances SET isPaid = :isPaid WHERE id = :attendanceId")
    suspend fun updateMessAttendancePaid(attendanceId: Long, isPaid: Boolean)

    // Settlements
    @Query("SELECT * FROM settlements ORDER BY settledDate DESC")
    fun getAllSettlements(): Flow<List<Settlement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: Settlement): Long
}
