package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.DebtTransfer
import com.example.data.models.Expense
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseGroup
import com.example.data.models.GroupType
import com.example.data.models.Student
import com.example.ui.components.CategoryChip
import com.example.ui.components.FourStatPillsRow
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.NeoDarkOutline
import com.example.ui.theme.NeoDarkText
import com.example.ui.theme.NeoMutedText
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.PastelLilacBackground
import com.example.ui.theme.PastelLilacDark
import com.example.ui.theme.PastelLilacSurface
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPink
import com.example.ui.theme.StatusNegativeRed
import com.example.ui.theme.StatusPositiveGreen
import com.example.ui.viewmodel.HostelMateViewModel

@Composable
fun TripsScreen(
    viewModel: HostelMateViewModel,
    onOpenAddExpense: (groupId: Long) -> Unit,
    onOpenSettleUp: (payerId: Long, receiverId: Long, amount: Double) -> Unit
) {
    val groups by viewModel.allGroups.collectAsStateWithLifecycle()
    val groupMembers by viewModel.allGroupMembers.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allSplits by viewModel.allSplits.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()

    val studentMap = allStudents.associateBy { it.id }

    val tripAndPartyGroups = groups.filter { it.type == GroupType.TRIP || it.type == GroupType.PARTY }
    var selectedGroupIndex by remember { mutableStateOf(0) }

    val activeGroup = tripAndPartyGroups.getOrNull(selectedGroupIndex)
        ?: tripAndPartyGroups.firstOrNull()

    Scaffold(
        containerColor = PastelLilacBackground,
        floatingActionButton = {
            if (activeGroup != null) {
                Surface(
                    onClick = { onOpenAddExpense(activeGroup.id) },
                    shape = RoundedCornerShape(18.dp),
                    color = NeoDarkOutline,
                    border = BorderStroke(2.dp, NeoDarkOutline),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .padding(bottom = 76.dp)
                        .size(54.dp)
                        .testTag("fab_add_trip_expense")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Add Trip Expense", tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PastelLilacBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Trip & Event Splitter",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = NeoDarkText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🏖️", fontSize = 24.sp)
                }
                Text(
                    text = "Multi-payer breakdown, custom shares & debt minimization matrix",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoMutedText
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (tripAndPartyGroups.isNotEmpty()) {
                item {
                    ScrollableTabRow(
                        selectedTabIndex = selectedGroupIndex.coerceIn(0, tripAndPartyGroups.size - 1),
                        containerColor = Color.Transparent,
                        contentColor = NeoDarkText,
                        divider = {},
                        edgePadding = 0.dp
                    ) {
                        tripAndPartyGroups.forEachIndexed { index, group ->
                            val isSelected = selectedGroupIndex == index
                            Surface(
                                onClick = { selectedGroupIndex = index },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) NeoDarkOutline else Color.White,
                                border = BorderStroke(1.5.dp, NeoDarkOutline),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = group.name,
                                    color = if (isSelected) Color.White else NeoDarkText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (activeGroup == null) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No Trips or Parties Found", fontWeight = FontWeight.Bold)
                            Text(
                                "Create a Trip or Party group in the Groups tab to manage shared vacations and celebrations.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoMutedText
                            )
                        }
                    }
                }
            } else {
                val groupExpenses = allExpenses.filter { it.groupId == activeGroup.id }
                val totalCost = groupExpenses.sumOf { it.amount }

                val memberIds = groupMembers.filter { it.groupId == activeGroup.id }.map { it.studentId }.toSet()
                val members = memberIds.mapNotNull { studentMap[it] }

                val paidByMap = mutableMapOf<Long, Double>()
                val shareMap = mutableMapOf<Long, Double>()
                members.forEach {
                    paidByMap[it.id] = 0.0
                    shareMap[it.id] = 0.0
                }

                groupExpenses.forEach { exp ->
                    paidByMap[exp.paidByStudentId] = (paidByMap[exp.paidByStudentId] ?: 0.0) + exp.amount
                }

                val tripSplits = allSplits.filter { split ->
                    groupExpenses.any { it.id == split.expenseId }
                }
                tripSplits.forEach { split ->
                    shareMap[split.studentId] = (shareMap[split.studentId] ?: 0.0) + split.amount
                }

                // 1. Total Trip Summary Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, NeoDarkOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = activeGroup.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = NeoDarkText
                                    )
                                    Text(
                                        text = "${members.size} participants • ${groupExpenses.size} items",
                                        fontSize = 11.sp,
                                        color = NeoMutedText
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹%,.0f".format(totalCost),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black,
                                        color = NeoDarkText
                                    )
                                    Text(
                                        text = "Total Trip Cost",
                                        fontSize = 11.sp,
                                        color = NeoMutedText
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. Participant Contributions
                item {
                    Text(
                        text = "Participant Contributions & Fair Shares:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = NeoDarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(members) { student ->
                    val paid = paidByMap[student.id] ?: 0.0
                    val share = shareMap[student.id] ?: 0.0
                    val net = paid - share

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StudentAvatar(name = student.name, avatarColorHex = student.avatarColorHex, size = 36.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeoDarkText)
                                Text(
                                    text = "Paid: ₹%,.0f • Share: ₹%,.0f".format(paid, share),
                                    fontSize = 11.sp,
                                    color = NeoMutedText
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (net >= 0) "+₹%,.0f".format(net) else "-₹%,.0f".format(kotlin.math.abs(net)),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (net >= 0) StatusPositiveGreen else StatusNegativeRed
                                )
                                Text(
                                    text = if (net >= 0) "gets back" else "owes",
                                    fontSize = 10.sp,
                                    color = NeoMutedText
                                )
                            }
                        }
                    }
                }

                // 3. Simplified Debt Matrix
                val tripDebts = viewModel.simplifiedDebts.value.filter {
                    memberIds.contains(it.fromStudent.id) && memberIds.contains(it.toStudent.id)
                }

                if (tripDebts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Simplified Settlements (Who Owes Whom):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = NeoDarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(tripDebts) { transfer ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = PastelMint),
                            border = BorderStroke(1.5.dp, NeoDarkOutline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StudentAvatar(name = transfer.fromStudent.name, avatarColorHex = transfer.fromStudent.avatarColorHex, size = 30.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(transfer.fromStudent.name.substringBefore(" "), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeoDarkText)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "pays", tint = NeoDarkOutline, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                StudentAvatar(name = transfer.toStudent.name, avatarColorHex = transfer.toStudent.avatarColorHex, size = 30.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(transfer.toStudent.name.substringBefore(" "), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeoDarkText)

                                Spacer(modifier = Modifier.weight(1f))

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹%,.0f".format(transfer.amount), fontWeight = FontWeight.Black, fontSize = 14.sp, color = NeoDarkText)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NeoDarkOutline,
                                        modifier = Modifier.clickable {
                                            onOpenSettleUp(transfer.fromStudent.id, transfer.toStudent.id, transfer.amount)
                                        }
                                    ) {
                                        Text(
                                            text = "Settle",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Itemized Expenses
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Itemized Trip Expenses:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = NeoDarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(groupExpenses) { exp ->
                    val payer = studentMap[exp.paidByStudentId]
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryChip(category = exp.category)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exp.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeoDarkText)
                                Text("Paid by ${payer?.name ?: "Someone"}", fontSize = 11.sp, color = NeoMutedText)
                            }
                            Text("₹%,.0f".format(exp.amount), fontWeight = FontWeight.Black, fontSize = 14.sp, color = NeoDarkText)
                        }
                    }
                }
            }
        }
    }
}
