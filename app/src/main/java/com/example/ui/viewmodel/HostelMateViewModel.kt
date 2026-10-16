package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.models.DebtTransfer
import com.example.data.models.Expense
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseGroup
import com.example.data.models.ExpenseSplit
import com.example.data.models.GroupDetail
import com.example.data.models.GroupMember
import com.example.data.models.GroupType
import com.example.data.models.MessAttendance
import com.example.data.models.MessBill
import com.example.data.models.Settlement
import com.example.data.models.SplitType
import com.example.data.models.Student
import com.example.data.repository.HostelMateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HostelMateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HostelMateRepository
    private val firebaseSync: com.example.data.repository.FirebaseSyncRepository

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = HostelMateRepository(database.hostelMateDao())
        firebaseSync = com.example.data.repository.FirebaseSyncRepository(application)
        viewModelScope.launch {
            repository.ensureSampleDataSeeded()
        }
    }

    fun syncAllToCloud() {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Syncing to Firebase..."
                val student = currentStudent.value
                if (student != null) {
                    firebaseSync.syncUserProfile(student)
                }
                firebaseSync.syncGroups(allGroups.value)
                firebaseSync.syncExpenses(allExpenses.value)
                firebaseSync.syncMessBills(allMessBills.value)
                _syncStatus.value = "Synced with Firebase Cloud!"
            } catch (e: Exception) {
                _syncStatus.value = "Sync failed: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    val allStudents: StateFlow<List<Student>> = repository.allStudents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentStudentId = MutableStateFlow<Long>(1L)
    val currentStudentId: StateFlow<Long> = _currentStudentId.asStateFlow()

    val currentStudent: StateFlow<Student?> = combine(allStudents, _currentStudentId) { students, id ->
        students.find { it.id == id } ?: students.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allGroups: StateFlow<List<ExpenseGroup>> = repository.allGroups.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allGroupMembers: StateFlow<List<GroupMember>> = repository.allGroupMembers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allExpenses: StateFlow<List<Expense>> = repository.allExpenses.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allSplits: StateFlow<List<ExpenseSplit>> = repository.allSplits.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allMessBills: StateFlow<List<MessBill>> = repository.allMessBills.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allAttendances: StateFlow<List<MessAttendance>> = repository.allAttendances.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allSettlements: StateFlow<List<Settlement>> = repository.allSettlements.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Simplified debts across all shared expenses
    val simplifiedDebts: StateFlow<List<DebtTransfer>> = combine(
        allStudents, allExpenses, allSplits, allSettlements
    ) { students, expenses, splits, settlements ->
        if (students.isEmpty()) emptyList()
        else repository.computeSimplifiedDebts(students, expenses, splits, settlements)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial balance for current active persona
    data class UserFinancialSummary(
        val totalIOwe: Double,
        val totalOthersOweMe: Double,
        val netBalance: Double
    )

    val userFinancialSummary: StateFlow<UserFinancialSummary> = combine(
        currentStudent, simplifiedDebts
    ) { student, debts ->
        if (student == null) {
            UserFinancialSummary(0.0, 0.0, 0.0)
        } else {
            var iOwe = 0.0
            var othersOwe = 0.0

            debts.forEach { transfer ->
                if (transfer.fromStudent.id == student.id) {
                    iOwe += transfer.amount
                }
                if (transfer.toStudent.id == student.id) {
                    othersOwe += transfer.amount
                }
            }
            UserFinancialSummary(
                totalIOwe = iOwe,
                totalOthersOweMe = othersOwe,
                netBalance = othersOwe - iOwe
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserFinancialSummary(0.0, 0.0, 0.0))

    // Monthly total expenses (all expenses sum)
    val monthlyTotalExpenses: StateFlow<Double> = allExpenses.combine(allMessBills) { expenses, messBills ->
        val expSum = expenses.sumOf { it.amount }
        val messSum = messBills.sumOf { it.totalAmount }
        expSum + messSum
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Category breakdown
    val categoryTotals: StateFlow<Map<ExpenseCategory, Double>> = allExpenses.combine(allMessBills) { expenses, messBills ->
        val map = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toMutableMap()

        // Also count mess bills under FOOD / OTHER
        val messTotal = messBills.sumOf { it.totalAmount }
        map[ExpenseCategory.FOOD] = (map[ExpenseCategory.FOOD] ?: 0.0) + messTotal
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Group Details with member balances
    val groupDetails: StateFlow<List<GroupDetail>> = combine(
        combine(allGroups, allGroupMembers, allStudents) { groups, members, students ->
            Triple(groups, members, students)
        },
        combine(allExpenses, allSplits, currentStudent) { expenses, splits, currentUser ->
            Triple(expenses, splits, currentUser)
        }
    ) { (groups, members, students), (expenses, splits, currentUser) ->
        val studentMap = students.associateBy { it.id }
        groups.map { group ->
            val groupMemberIds = members.filter { it.groupId == group.id }.map { it.studentId }.toSet()
            val groupMembers = groupMemberIds.mapNotNull { studentMap[it] }
            val groupExpenses = expenses.filter { it.groupId == group.id }
            val total = groupExpenses.sumOf { it.amount }

            var myBalance = 0.0
            if (currentUser != null) {
                val paidByMe = groupExpenses.filter { it.paidByStudentId == currentUser.id }.sumOf { it.amount }
                val mySplits = splits.filter { split ->
                    val exp = groupExpenses.find { it.id == split.expenseId }
                    exp != null && split.studentId == currentUser.id
                }.sumOf { it.amount }
                myBalance = paidByMe - mySplits
            }

            GroupDetail(
                group = group,
                members = groupMembers,
                totalExpenses = total,
                myBalanceInGroup = myBalance
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun switchCurrentStudent(studentId: Long) {
        _currentStudentId.value = studentId
    }

    fun createGroup(name: String, type: GroupType, description: String, memberIds: List<Long>) {
        viewModelScope.launch {
            repository.createGroup(name, type, description, memberIds)
        }
    }

    fun addExpense(
        groupId: Long,
        title: String,
        amount: Double,
        paidByStudentId: Long,
        category: ExpenseCategory,
        selectedMemberIds: List<Long>,
        splitType: SplitType,
        customAmounts: Map<Long, Double> = emptyMap()
    ) {
        viewModelScope.launch {
            val splitMap = when (splitType) {
                SplitType.EQUAL -> {
                    val count = selectedMemberIds.size.coerceAtLeast(1)
                    val share = amount / count
                    selectedMemberIds.associateWith { share }
                }
                SplitType.CUSTOM -> {
                    customAmounts
                }
            }
            repository.addExpenseWithSplits(
                groupId = groupId,
                title = title,
                amount = amount,
                paidByStudentId = paidByStudentId,
                category = category,
                splitMap = splitMap,
                splitType = splitType
            )
        }
    }

    fun createMessBill(
        groupId: Long,
        monthYear: String,
        totalAmount: Double,
        totalDays: Int,
        attendanceDays: Map<Long, Int>,
        notesMap: Map<Long, String>
    ) {
        viewModelScope.launch {
            repository.createMessBillWithAttendance(
                groupId = groupId,
                monthYear = monthYear,
                totalAmount = totalAmount,
                totalDays = totalDays,
                attendanceDays = attendanceDays,
                notesMap = notesMap
            )
        }
    }

    fun toggleMessPaid(attendanceId: Long, currentPaidStatus: Boolean) {
        viewModelScope.launch {
            repository.markMessPaid(attendanceId, !currentPaidStatus)
        }
    }

    fun settleDebt(
        fromStudentId: Long,
        toStudentId: Long,
        groupId: Long?,
        amount: Double,
        paymentMode: String
    ) {
        viewModelScope.launch {
            repository.recordSettlement(
                fromStudentId = fromStudentId,
                toStudentId = toStudentId,
                groupId = groupId,
                amount = amount,
                paymentMode = paymentMode
            )
        }
    }

    fun deleteGroup(group: ExpenseGroup) {
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun deleteMessBill(bill: MessBill) {
        viewModelScope.launch {
            repository.deleteMessBill(bill)
        }
    }
}
