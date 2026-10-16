package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.models.Expense
import com.example.data.models.ExpenseGroup
import com.example.data.models.MessBill
import com.example.data.models.Student
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseSyncRepository(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    val currentUser get() = auth.currentUser

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firebase.")
    }

    suspend fun syncUserProfile(student: Student) {
        val uid = requireUserId()
        val userDoc = db.collection("users").document(uid)
        val data = hashMapOf(
            "userId" to uid,
            "name" to student.name,
            "roomNumber" to student.roomNumber,
            "email" to (currentUser?.email ?: student.email),
            "upiId" to student.upiId,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        userDoc.set(data).await()
        Log.d("FirebaseSync", "Synced user profile for $uid")
    }

    suspend fun syncGroups(groups: List<ExpenseGroup>) {
        val uid = requireUserId()
        val groupsCol = db.collection("users").document(uid).collection("groups")
        for (g in groups) {
            val groupData = hashMapOf(
                "id" to g.id.toString(),
                "userId" to uid,
                "name" to g.name,
                "type" to g.type.name,
                "description" to g.description,
                "inviteCode" to g.inviteCode,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            groupsCol.document(g.id.toString()).set(groupData).await()
        }
        Log.d("FirebaseSync", "Synced ${groups.size} groups for $uid")
    }

    suspend fun syncExpenses(expenses: List<Expense>) {
        val uid = requireUserId()
        val expensesCol = db.collection("users").document(uid).collection("expenses")
        for (e in expenses) {
            val expData = hashMapOf(
                "id" to e.id.toString(),
                "userId" to uid,
                "groupId" to e.groupId,
                "title" to e.title,
                "amount" to e.amount,
                "category" to e.category.name,
                "date" to e.date,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            expensesCol.document(e.id.toString()).set(expData).await()
        }
        Log.d("FirebaseSync", "Synced ${expenses.size} expenses for $uid")
    }

    suspend fun syncMessBills(messBills: List<MessBill>) {
        val uid = requireUserId()
        val messCol = db.collection("users").document(uid).collection("messBills")
        for (m in messBills) {
            val billData = hashMapOf(
                "id" to m.id.toString(),
                "userId" to uid,
                "monthYear" to m.monthYear,
                "totalAmount" to m.totalAmount,
                "totalDays" to m.totalDays,
                "dailyRate" to m.dailyRate,
                "notes" to m.notes,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            messCol.document(m.id.toString()).set(billData).await()
        }
        Log.d("FirebaseSync", "Synced ${messBills.size} mess bills for $uid")
    }
}
