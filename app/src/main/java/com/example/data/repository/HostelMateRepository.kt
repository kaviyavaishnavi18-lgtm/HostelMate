package com.example.data.repository

import com.example.data.dao.HostelMateDao
import com.example.data.models.DebtTransfer
import com.example.data.models.Expense
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseGroup
import com.example.data.models.ExpenseSplit
import com.example.data.models.GroupMember
import com.example.data.models.GroupType
import com.example.data.models.MessAttendance
import com.example.data.models.MessBill
import com.example.data.models.Settlement
import com.example.data.models.SplitType
import com.example.data.models.Student
import com.example.data.models.StudentBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

class HostelMateRepository(private val dao: HostelMateDao) {

    val allStudents: Flow<List<Student>> = dao.getAllStudents()
    val allGroups: Flow<List<ExpenseGroup>> = dao.getAllGroups()
    val allGroupMembers: Flow<List<GroupMember>> = dao.getAllGroupMembers()
    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()
    val allSplits: Flow<List<ExpenseSplit>> = dao.getAllSplits()
    val allMessBills: Flow<List<MessBill>> = dao.getAllMessBills()
    val allAttendances: Flow<List<MessAttendance>> = dao.getAllMessAttendances()
    val allSettlements: Flow<List<Settlement>> = dao.getAllSettlements()

    fun getGroupExpenses(groupId: Long): Flow<List<Expense>> = dao.getExpensesByGroup(groupId)
    fun getGroupMembers(groupId: Long): Flow<List<Student>> = dao.getGroupMembers(groupId)
    fun getMessAttendances(messBillId: Long): Flow<List<MessAttendance>> = dao.getMessAttendances(messBillId)

    suspend fun ensureSampleDataSeeded() {
        val currentStudents = dao.getAllStudents().first()
        if (currentStudents.isNotEmpty()) return

        // 1. Seed 5 Roommate Profiles
        val students = listOf(
            Student(id = 1, name = "Rahul Sharma", roomNumber = "Room 204", email = "rahul@hostel.edu", avatarColorHex = "#10B981", upiId = "rahul@okaxis"),
            Student(id = 2, name = "Priya Patel", roomNumber = "Room 204", email = "priya@hostel.edu", avatarColorHex = "#EC4899", upiId = "priya@okhdfc"),
            Student(id = 3, name = "Arun Kumar", roomNumber = "Room 204", email = "arun@hostel.edu", avatarColorHex = "#3B82F6", upiId = "arun@oksbi"),
            Student(id = 4, name = "Sneha Reddy", roomNumber = "Room 205", email = "sneha@hostel.edu", avatarColorHex = "#8B5CF6", upiId = "sneha@okicici"),
            Student(id = 5, name = "Vikram Singh", roomNumber = "Room 205", email = "vikram@hostel.edu", avatarColorHex = "#F59E0B", upiId = "vikram@paytm")
        )
        dao.insertStudents(students)

        // 2. Seed 4 Groups
        val g1 = ExpenseGroup(id = 1, name = "Room 204 Essentials", type = GroupType.ROOM, description = "Daily room expenses, high speed WiFi, water cans and snacks", inviteCode = "HM-ROOM-204")
        val g2 = ExpenseGroup(id = 2, name = "Goa Trip 2026", type = GroupType.TRIP, description = "College beach trip - resort stay, cabs, dinner & water sports", inviteCode = "HM-GOA-2026")
        val g3 = ExpenseGroup(id = 3, name = "Hostel Mess - Block B", type = GroupType.MESS, description = "Monthly catering mess bill with attendance adjustment", inviteCode = "HM-MESS-OCT")
        val g4 = ExpenseGroup(id = 4, name = "Arun's Birthday Party", type = GroupType.PARTY, description = "Midnight cake cutting, snacks, banners & group gift", inviteCode = "HM-BDAY-ARUN")

        dao.insertGroup(g1)
        dao.insertGroup(g2)
        dao.insertGroup(g3)
        dao.insertGroup(g4)

        // 3. Seed Members
        val members = listOf(
            // Room 204
            GroupMember(1, 1, "ADMIN"), GroupMember(1, 2, "MEMBER"), GroupMember(1, 3, "MEMBER"),
            // Goa Trip
            GroupMember(2, 1, "ADMIN"), GroupMember(2, 2, "MEMBER"), GroupMember(2, 3, "MEMBER"), GroupMember(2, 4, "MEMBER"),
            // Mess
            GroupMember(3, 1, "ADMIN"), GroupMember(3, 2, "MEMBER"), GroupMember(3, 3, "MEMBER"), GroupMember(3, 4, "MEMBER"), GroupMember(3, 5, "MEMBER"),
            // Party
            GroupMember(4, 1, "ADMIN"), GroupMember(4, 2, "MEMBER"), GroupMember(4, 4, "MEMBER"), GroupMember(4, 5, "MEMBER")
        )
        dao.insertGroupMembers(members)

        // 4. Seed Expenses & Splits
        // Exp 1: Goa Beach Resort Stay ₹4,000 paid by Rahul
        val exp1Id = dao.insertExpense(
            Expense(
                id = 1,
                groupId = 2,
                title = "Beach Villa Stay & AC",
                amount = 4000.0,
                paidByStudentId = 1, // Rahul
                category = ExpenseCategory.TRIP,
                date = System.currentTimeMillis() - 86400000L * 4
            )
        )
        dao.insertSplits(listOf(
            ExpenseSplit(expenseId = exp1Id, studentId = 1, amount = 1000.0),
            ExpenseSplit(expenseId = exp1Id, studentId = 2, amount = 1000.0),
            ExpenseSplit(expenseId = exp1Id, studentId = 3, amount = 1000.0),
            ExpenseSplit(expenseId = exp1Id, studentId = 4, amount = 1000.0)
        ))

        // Exp 2: Goa Seafood & Cafe ₹2,000 paid by Priya
        val exp2Id = dao.insertExpense(
            Expense(
                id = 2,
                groupId = 2,
                title = "Shack Dinner & Drinks",
                amount = 2000.0,
                paidByStudentId = 2, // Priya
                category = ExpenseCategory.FOOD,
                date = System.currentTimeMillis() - 86400000L * 3
            )
        )
        dao.insertSplits(listOf(
            ExpenseSplit(expenseId = exp2Id, studentId = 1, amount = 500.0),
            ExpenseSplit(expenseId = exp2Id, studentId = 2, amount = 500.0),
            ExpenseSplit(expenseId = exp2Id, studentId = 3, amount = 500.0),
            ExpenseSplit(expenseId = exp2Id, studentId = 4, amount = 500.0)
        ))

        // Exp 3: Goa Cab ₹1,200 paid by Arun
        val exp3Id = dao.insertExpense(
            Expense(
                id = 3,
                groupId = 2,
                title = "Airport & Beach Cab",
                amount = 1200.0,
                paidByStudentId = 3, // Arun
                category = ExpenseCategory.TRIP,
                date = System.currentTimeMillis() - 86400000L * 2
            )
        )
        dao.insertSplits(listOf(
            ExpenseSplit(expenseId = exp3Id, studentId = 1, amount = 300.0),
            ExpenseSplit(expenseId = exp3Id, studentId = 2, amount = 300.0),
            ExpenseSplit(expenseId = exp3Id, studentId = 3, amount = 300.0),
            ExpenseSplit(expenseId = exp3Id, studentId = 4, amount = 300.0)
        ))

        // Exp 4: Room 204 Fiber Wi-Fi ₹899 paid by Rahul
        val exp4Id = dao.insertExpense(
            Expense(
                id = 4,
                groupId = 1,
                title = "Monthly 100Mbps Wi-Fi",
                amount = 899.0,
                paidByStudentId = 1, // Rahul
                category = ExpenseCategory.RENT_UTILITY,
                date = System.currentTimeMillis() - 86400000L * 5
            )
        )
        val share4 = (899.0 / 3.0)
        dao.insertSplits(listOf(
            ExpenseSplit(expenseId = exp4Id, studentId = 1, amount = share4),
            ExpenseSplit(expenseId = exp4Id, studentId = 2, amount = share4),
            ExpenseSplit(expenseId = exp4Id, studentId = 3, amount = share4)
        ))

        // Exp 5: Water Cans & Groceries ₹450 paid by Priya
        val exp5Id = dao.insertExpense(
            Expense(
                id = 5,
                groupId = 1,
                title = "Water Cans & Room Cleaning",
                amount = 450.0,
                paidByStudentId = 2, // Priya
                category = ExpenseCategory.GROCERY,
                date = System.currentTimeMillis() - 86400000L * 1
            )
        )
        dao.insertSplits(listOf(
            ExpenseSplit(expenseId = exp5Id, studentId = 1, amount = 150.0),
            ExpenseSplit(expenseId = exp5Id, studentId = 2, amount = 150.0),
            ExpenseSplit(expenseId = exp5Id, studentId = 3, amount = 150.0)
        ))

        // Exp 6: Arun's Birthday Cake ₹750 paid by Sneha
        val exp6Id = dao.insertExpense(
            Expense(
                id = 6,
                groupId = 4,
                title = "Chocolate Truffle Cake & Candles",
                amount = 750.0,
                paidByStudentId = 4, // Sneha
                category = ExpenseCategory.PARTY,
                date = System.currentTimeMillis() - 86400000L * 7
            )
        )
        dao.insertSplits(listOf(
            ExpenseSplit(expenseId = exp6Id, studentId = 1, amount = 250.0),
            ExpenseSplit(expenseId = exp6Id, studentId = 2, amount = 250.0),
            ExpenseSplit(expenseId = exp6Id, studentId = 4, amount = 250.0)
        ))

        // 5. Seed Mess Bill with Absent-Day attendance calculation
        // Total Mess Bill = ₹12,000 for October (30 days total)
        // Rahul: 30 days, Priya: 25 days, Arun: 15 days (absent 15 days), Sneha: 28 days, Vikram: 22 days
        // Total student-days = 30 + 25 + 15 + 28 + 22 = 120 days
        // Per day cost = ₹12,000 / 120 = ₹100 per day!
        val messBillId = dao.insertMessBill(
            MessBill(
                id = 1,
                groupId = 3,
                monthYear = "October 2026",
                totalAmount = 12000.0,
                totalDays = 30,
                dailyRate = 100.0,
                notes = "Calculated per day of mess attendance. Absent days deducted automatically."
            )
        )
        dao.insertMessAttendances(listOf(
            MessAttendance(messBillId = messBillId, studentId = 1, daysAttended = 30, calculatedAmount = 3000.0, isPaid = true, notes = "Full month (Paid via UPI)"),
            MessAttendance(messBillId = messBillId, studentId = 2, daysAttended = 25, calculatedAmount = 2500.0, isPaid = true, notes = "5 days home (Paid via GPay)"),
            MessAttendance(messBillId = messBillId, studentId = 3, daysAttended = 15, calculatedAmount = 1500.0, isPaid = false, notes = "15 days sick leave (Pending)"),
            MessAttendance(messBillId = messBillId, studentId = 4, daysAttended = 28, calculatedAmount = 2800.0, isPaid = false, notes = "2 days symposium (Pending)"),
            MessAttendance(messBillId = messBillId, studentId = 5, daysAttended = 22, calculatedAmount = 2200.0, isPaid = false, notes = "8 days sports meet (Pending)")
        ))
    }

    // Smart Debt Minimization (Greedy Algorithm)
    // Takes all expenses and settlements within a group (or overall) and computes the minimal direct transfers needed
    fun computeSimplifiedDebts(
        students: List<Student>,
        expenses: List<Expense>,
        splits: List<ExpenseSplit>,
        settlements: List<Settlement>
    ): List<DebtTransfer> {
        val studentMap = students.associateBy { it.id }
        val netBalance = mutableMapOf<Long, Double>()
        students.forEach { netBalance[it.id] = 0.0 }

        // 1. Credit the payer
        expenses.forEach { exp ->
            netBalance[exp.paidByStudentId] = (netBalance[exp.paidByStudentId] ?: 0.0) + exp.amount
        }

        // 2. Debit the participants for their share
        splits.forEach { split ->
            netBalance[split.studentId] = (netBalance[split.studentId] ?: 0.0) - split.amount
        }

        // 3. Apply settlements
        settlements.forEach { settlement ->
            netBalance[settlement.fromStudentId] = (netBalance[settlement.fromStudentId] ?: 0.0) + settlement.amount
            netBalance[settlement.toStudentId] = (netBalance[settlement.toStudentId] ?: 0.0) - settlement.amount
        }

        // Separate debtors (net < -0.01) and creditors (net > 0.01)
        val debtors = mutableListOf<Pair<Long, Double>>()
        val creditors = mutableListOf<Pair<Long, Double>>()

        netBalance.forEach { (id, balance) ->
            if (balance < -0.01) {
                debtors.add(id to -balance)
            } else if (balance > 0.01) {
                creditors.add(id to balance)
            }
        }

        val result = mutableListOf<DebtTransfer>()
        var dIdx = 0
        var cIdx = 0

        while (dIdx < debtors.size && cIdx < creditors.size) {
            val (debtorId, debtorOwes) = debtors[dIdx]
            val (creditorId, creditorDue) = creditors[cIdx]

            val settleAmount = min(debtorOwes, creditorDue)
            val debtorStudent = studentMap[debtorId]
            val creditorStudent = studentMap[creditorId]

            if (debtorStudent != null && creditorStudent != null && settleAmount > 0.01) {
                result.add(
                    DebtTransfer(
                        fromStudent = debtorStudent,
                        toStudent = creditorStudent,
                        amount = settleAmount
                    )
                )
            }

            debtors[dIdx] = debtorId to (debtorOwes - settleAmount)
            creditors[cIdx] = creditorId to (creditorDue - settleAmount)

            if (debtors[dIdx].second < 0.01) dIdx++
            if (creditors[cIdx].second < 0.01) cIdx++
        }

        return result
    }

    suspend fun createGroup(name: String, type: GroupType, description: String, memberIds: List<Long>): Long {
        val code = "HM-" + name.take(4).uppercase().replace(" ", "") + "-" + Random.nextInt(100, 999)
        val group = ExpenseGroup(
            name = name,
            type = type,
            description = description,
            inviteCode = code
        )
        val groupId = dao.insertGroup(group)
        val groupMembers = memberIds.mapIndexed { index, studentId ->
            GroupMember(
                groupId = groupId,
                studentId = studentId,
                role = if (index == 0) "ADMIN" else "MEMBER"
            )
        }
        dao.insertGroupMembers(groupMembers)
        return groupId
    }

    suspend fun addExpenseWithSplits(
        groupId: Long,
        title: String,
        amount: Double,
        paidByStudentId: Long,
        category: ExpenseCategory,
        splitMap: Map<Long, Double>,
        splitType: SplitType
    ): Long {
        val expense = Expense(
            groupId = groupId,
            title = title,
            amount = amount,
            paidByStudentId = paidByStudentId,
            category = category,
            date = System.currentTimeMillis(),
            splitType = splitType
        )
        val expenseId = dao.insertExpense(expense)
        val splits = splitMap.map { (studentId, share) ->
            ExpenseSplit(
                expenseId = expenseId,
                studentId = studentId,
                amount = share
            )
        }
        dao.insertSplits(splits)
        return expenseId
    }

    suspend fun createMessBillWithAttendance(
        groupId: Long,
        monthYear: String,
        totalAmount: Double,
        totalDays: Int,
        attendanceDays: Map<Long, Int>,
        notesMap: Map<Long, String>
    ): Long {
        val totalStudentDays = attendanceDays.values.sum().coerceAtLeast(1)
        val dailyRate = totalAmount / totalStudentDays

        val bill = MessBill(
            groupId = groupId,
            monthYear = monthYear,
            totalAmount = totalAmount,
            totalDays = totalDays,
            dailyRate = dailyRate,
            notes = "Total bill ₹${"%.0f".format(totalAmount)} split across $totalStudentDays total student-days."
        )
        val billId = dao.insertMessBill(bill)

        val attendances = attendanceDays.map { (studentId, days) ->
            val share = days * dailyRate
            MessAttendance(
                messBillId = billId,
                studentId = studentId,
                daysAttended = days,
                calculatedAmount = share,
                isPaid = false,
                notes = notesMap[studentId] ?: "$days days attended"
            )
        }
        dao.insertMessAttendances(attendances)
        return billId
    }

    suspend fun markMessPaid(attendanceId: Long, isPaid: Boolean) {
        dao.updateMessAttendancePaid(attendanceId, isPaid)
    }

    suspend fun recordSettlement(
        fromStudentId: Long,
        toStudentId: Long,
        groupId: Long?,
        amount: Double,
        paymentMode: String
    ): Long {
        val settlement = Settlement(
            fromStudentId = fromStudentId,
            toStudentId = toStudentId,
            groupId = groupId,
            amount = amount,
            settledDate = System.currentTimeMillis(),
            paymentMode = paymentMode
        )
        return dao.insertSettlement(settlement)
    }

    suspend fun deleteGroup(group: ExpenseGroup) = dao.deleteGroup(group)
    suspend fun deleteExpense(expense: Expense) = dao.deleteExpense(expense)
    suspend fun deleteMessBill(bill: MessBill) = dao.deleteMessBill(bill)
}
