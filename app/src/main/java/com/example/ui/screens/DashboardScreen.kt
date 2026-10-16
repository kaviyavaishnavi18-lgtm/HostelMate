package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.models.DebtTransfer
import com.example.data.models.Expense
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseGroup
import com.example.data.models.GroupDetail
import com.example.data.models.GroupType
import com.example.data.models.Student
import com.example.ui.components.ArrowSquareButton
import com.example.ui.components.CategoryChip
import com.example.ui.components.FourStatPillsRow
import com.example.ui.components.GroupTypeBadge
import com.example.ui.components.PersonSwitcherDialog
import com.example.ui.components.RupeeText
import com.example.ui.components.SquircleIconButton
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
import com.example.ui.theme.StatusNegativeContainer
import com.example.ui.theme.StatusNegativeRed
import com.example.ui.theme.StatusPositiveContainer
import com.example.ui.theme.StatusPositiveGreen
import com.example.ui.viewmodel.HostelMateViewModel

@Composable
fun DashboardScreen(
    viewModel: HostelMateViewModel,
    onNavigateToGroups: () -> Unit,
    onNavigateToMess: () -> Unit,
    onNavigateToTrips: () -> Unit,
    onOpenAddExpense: (groupId: Long?) -> Unit,
    onOpenCreateGroup: () -> Unit,
    onOpenSettleUp: (payerId: Long, receiverId: Long, amount: Double) -> Unit
) {
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val financialSummary by viewModel.userFinancialSummary.collectAsStateWithLifecycle()
    val monthlyTotal by viewModel.monthlyTotalExpenses.collectAsStateWithLifecycle()
    val categoryTotals by viewModel.categoryTotals.collectAsStateWithLifecycle()
    val groupDetails by viewModel.groupDetails.collectAsStateWithLifecycle()
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val splits by viewModel.allSplits.collectAsStateWithLifecycle()
    val simplifiedDebts by viewModel.simplifiedDebts.collectAsStateWithLifecycle()

    var showPersonSwitcher by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    if (showPersonSwitcher) {
        PersonSwitcherDialog(
            students = allStudents,
            currentStudentId = currentStudent?.id ?: 1L,
            onSelectStudent = { viewModel.switchCurrentStudent(it.id) },
            onDismiss = { showPersonSwitcher = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelLilacBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 1. Top Bar matching the reference UI (Menu on left, Greetings, Bell & Profile on right)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Menu Squircle Button
                SquircleIconButton(
                    icon = Icons.Default.Menu,
                    onClick = onNavigateToGroups,
                    backgroundColor = PastelPeach
                )

                // Right controls: Persona Switcher & Avatar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SquircleIconButton(
                        icon = Icons.Default.SwapHoriz,
                        onClick = { showPersonSwitcher = true },
                        backgroundColor = PastelPeach,
                        modifier = Modifier.testTag("persona_switcher_button")
                    )

                    // Avatar with neo border
                    StudentAvatar(
                        name = currentStudent?.name ?: "Student",
                        avatarColorHex = currentStudent?.avatarColorHex ?: "#10B981",
                        size = 44.dp,
                        modifier = Modifier.clickable { showPersonSwitcher = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Greeting text: "Hi [Name] 👋", "Good Evening!"
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hi ${currentStudent?.name?.substringBefore(" ") ?: "Hosteller"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = NeoDarkText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "👋", fontSize = 28.sp)
                }
                Text(
                    text = "${currentStudent?.roomNumber ?: "Room 204"} • Happy Hostel Life!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeoMutedText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Horizontal Category Pills ("All", "Mess", "Room 204", "Trips", "Party")
            val filterOptions = listOf("All", "Mess", "Room 204", "Trips", "Party")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = selectedCategoryFilter == filter
                    Surface(
                        onClick = { selectedCategoryFilter = filter },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) NeoDarkOutline else Color.White,
                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                        modifier = Modifier.testTag("filter_pill_$filter")
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else NeoDarkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Hero Feature Card (designed exactly after the reference image's card)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, NeoDarkOutline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_feature_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Top Row of card: Category Tag pill & Net Status Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.5.dp, NeoDarkOutline)
                        ) {
                            Text(
                                text = "Smart Hostel Splitter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoDarkText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (financialSummary.netBalance >= 0) PastelMint else PastelPink,
                            border = BorderStroke(1.5.dp, NeoDarkOutline)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (financialSummary.netBalance >= 0) "+₹%,.0f".format(financialSummary.netBalance)
                                    else "-₹%,.0f".format(kotlin.math.abs(financialSummary.netBalance)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeoDarkText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Center Graphic: Pastel sticker character asset
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(PastelLilacSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hostel_pastel_character_1792142708996),
                            contentDescription = "Hostel Living",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(150.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Row: Title, Subtitle, and Black Squircle Arrow Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fair Roommate Finances",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = NeoDarkText
                            )
                            Text(
                                text = "Eliminate awkward reminders. Split mess, rent & trips.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoMutedText
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        ArrowSquareButton(
                            onClick = { onOpenAddExpense(null) },
                            size = 42.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Four Stat Pills Row (Matching reference design: Weight, Gender, Age, Time)
        item {
            FourStatPillsRow(
                stat1Label = "You Owe",
                stat1Val = "₹%,.0f".format(financialSummary.totalIOwe),
                stat1Bg = PastelPink,

                stat2Label = "You're Owed",
                stat2Val = "₹%,.0f".format(financialSummary.totalOthersOweMe),
                stat2Bg = PastelMint,

                stat3Label = "Monthly Mess",
                stat3Val = "₹%,.0f".format(monthlyTotal),
                stat3Bg = PastelPeach,

                stat4Label = "Net Status",
                stat4Val = if (financialSummary.netBalance >= 0) "+₹%,.0f".format(financialSummary.netBalance)
                else "-₹%,.0f".format(kotlin.math.abs(financialSummary.netBalance)),
                stat4Bg = PastelBlue
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpenAddExpense(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeoDarkOutline),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, NeoDarkOutline),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("quick_add_expense_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToMess,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPeach),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, NeoDarkOutline),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("quick_split_mess_button")
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = NeoDarkText, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mess Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeoDarkText)
                }

                Button(
                    onClick = onOpenCreateGroup,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, NeoDarkOutline),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("quick_create_group_button")
                ) {
                    Text("+ Group", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeoDarkText)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 6. Suggested Settlements in Neo Card Style
        val relevantDebts = simplifiedDebts.filter {
            it.fromStudent.id == currentStudent?.id || it.toStudent.id == currentStudent?.id
        }
        if (relevantDebts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Suggested Settlements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NeoDarkText
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PastelPeach,
                        border = BorderStroke(1.5.dp, NeoDarkOutline)
                    ) {
                        Text(
                            text = "Minimal Transfers",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoDarkText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(relevantDebts) { transfer ->
                val amIPaying = transfer.fromStudent.id == currentStudent?.id
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (amIPaying) PastelPink else PastelMint),
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
                        StudentAvatar(
                            name = if (amIPaying) transfer.toStudent.name else transfer.fromStudent.name,
                            avatarColorHex = if (amIPaying) transfer.toStudent.avatarColorHex else transfer.fromStudent.avatarColorHex,
                            size = 36.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (amIPaying) "You owe ${transfer.toStudent.name}"
                                else "${transfer.fromStudent.name} owes you",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeoDarkText
                            )
                            Text(
                                text = if (amIPaying) "UPI: ${transfer.toStudent.upiId}"
                                else "Room: ${transfer.fromStudent.roomNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoMutedText
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹%,.0f".format(transfer.amount),
                                fontWeight = FontWeight.Black,
                                color = NeoDarkText,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeoDarkOutline,
                                border = BorderStroke(1.dp, NeoDarkOutline),
                                modifier = Modifier
                                    .clickable {
                                        onOpenSettleUp(
                                            transfer.fromStudent.id,
                                            transfer.toStudent.id,
                                            transfer.amount
                                        )
                                    }
                                    .testTag("settle_transfer_${transfer.fromStudent.id}_${transfer.toStudent.id}")
                            ) {
                                Text(
                                    text = "Settle",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // 7. Hostel Groups (Cards styled after the reference UI)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Hostel Groups",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = NeoDarkText
                )
                Surface(
                    onClick = onNavigateToGroups,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, NeoDarkOutline)
                ) {
                    Text(
                        text = "View All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoDarkText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(groupDetails) { detail ->
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onNavigateToGroups() }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GroupTypeBadge(type = detail.group.type)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PastelPeach,
                                    border = BorderStroke(1.dp, NeoDarkOutline)
                                ) {
                                    Text(
                                        text = "${detail.members.size} ppl",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = detail.group.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = NeoDarkText,
                                maxLines = 1
                            )
                            Text(
                                text = detail.group.description,
                                fontSize = 11.sp,
                                color = NeoMutedText,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Spent", fontSize = 10.sp, color = NeoMutedText)
                                    Text("₹%,.0f".format(detail.totalExpenses), fontWeight = FontWeight.Black, fontSize = 13.sp, color = NeoDarkText)
                                }
                                ArrowSquareButton(
                                    onClick = onNavigateToGroups,
                                    size = 34.dp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 8. Recent Shared Expenses
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = NeoDarkText
                )
                Text(
                    text = "${expenses.size} bills",
                    fontSize = 12.sp,
                    color = NeoMutedText
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        val studentMap = allStudents.associateBy { it.id }
        val groupMap = groupDetails.associate { it.group.id to it.group }

        items(expenses.take(5)) { expense ->
            val payer = studentMap[expense.paidByStudentId]
            val group = groupMap[expense.groupId]
            val mySplit = splits.find { it.expenseId == expense.id && it.studentId == currentStudent?.id }

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
                    CategoryChip(category = expense.category)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = expense.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeoDarkText
                        )
                        Text(
                            text = "${group?.name ?: "Group"} • Paid by ${payer?.name?.substringBefore(" ") ?: "Member"}",
                            fontSize = 11.sp,
                            color = NeoMutedText
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹%,.0f".format(expense.amount),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = NeoDarkText
                        )
                        if (mySplit != null) {
                            Text(
                                text = "Your: ₹%,.0f".format(mySplit.amount),
                                fontSize = 11.sp,
                                color = NeoDarkOutline,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
