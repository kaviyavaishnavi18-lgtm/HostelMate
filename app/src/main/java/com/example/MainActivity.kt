package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.CreateGroupDialog
import com.example.ui.components.CreateMessBillDialog
import com.example.ui.components.SettleUpDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.screens.MessSplitterScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TripsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeoDarkOutline
import com.example.ui.theme.NeoDarkText
import com.example.ui.theme.NeoMutedText
import com.example.ui.theme.PastelLilacBackground
import com.example.ui.theme.PastelPeach
import com.example.ui.viewmodel.HostelMateViewModel

enum class AppNavDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Home", Icons.Default.Home),
    GROUPS("Groups", Icons.Default.Group),
    MESS("Mess", Icons.Default.Restaurant),
    TRIPS("Trips", Icons.Default.BeachAccess),
    PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HostelMateApp()
            }
        }
    }
}

@Composable
fun HostelMateApp(viewModel: HostelMateViewModel = viewModel()) {
    var currentDestination by remember { mutableStateOf(AppNavDestination.DASHBOARD) }

    // Dialog States
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var addExpenseGroupId by remember { mutableStateOf<Long?>(null) }

    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showCreateMessBillDialog by remember { mutableStateOf(false) }

    var showSettleUpDialog by remember { mutableStateOf(false) }
    var settlePayerId by remember { mutableStateOf(1L) }
    var settleReceiverId by remember { mutableStateOf<Long?>(null) }
    var settleAmount by remember { mutableStateOf<Double?>(null) }

    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()

    BackHandler(enabled = currentDestination != AppNavDestination.DASHBOARD) {
        currentDestination = AppNavDestination.DASHBOARD
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelLilacBackground)
    ) {
        // Main Screen View
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentDestination) {
                AppNavDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToGroups = { currentDestination = AppNavDestination.GROUPS },
                    onNavigateToMess = { currentDestination = AppNavDestination.MESS },
                    onNavigateToTrips = { currentDestination = AppNavDestination.TRIPS },
                    onOpenAddExpense = { groupId ->
                        addExpenseGroupId = groupId
                        showAddExpenseDialog = true
                    },
                    onOpenCreateGroup = { showCreateGroupDialog = true },
                    onOpenSettleUp = { payerId, receiverId, amount ->
                        settlePayerId = payerId
                        settleReceiverId = receiverId
                        settleAmount = amount
                        showSettleUpDialog = true
                    }
                )
                AppNavDestination.GROUPS -> GroupsScreen(
                    viewModel = viewModel,
                    onOpenCreateGroup = { showCreateGroupDialog = true },
                    onOpenAddExpense = { groupId ->
                        addExpenseGroupId = groupId
                        showAddExpenseDialog = true
                    }
                )
                AppNavDestination.MESS -> MessSplitterScreen(
                    viewModel = viewModel,
                    onOpenCreateMessBill = { showCreateMessBillDialog = true }
                )
                AppNavDestination.TRIPS -> TripsScreen(
                    viewModel = viewModel,
                    onOpenAddExpense = { groupId ->
                        addExpenseGroupId = groupId
                        showAddExpenseDialog = true
                    },
                    onOpenSettleUp = { payerId, receiverId, amount ->
                        settlePayerId = payerId
                        settleReceiverId = receiverId
                        settleAmount = amount
                        showSettleUpDialog = true
                    }
                )
                AppNavDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
        }

        // Custom Floating Neo-Pill Bottom Navigation Bar (Matching Image Reference)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp, start = 20.dp, end = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                border = BorderStroke(2.dp, NeoDarkOutline),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("floating_nav_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppNavDestination.values().forEach { destination ->
                        val isSelected = currentDestination == destination

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) NeoDarkOutline else Color.Transparent)
                                .clickable { currentDestination = destination }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("nav_item_${destination.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                    tint = if (isSelected) Color.White else NeoDarkText,
                                    modifier = Modifier.size(20.dp)
                                )
                                if (isSelected) {
                                    Text(
                                        text = " " + destination.label,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            groups = allGroups,
            students = allStudents,
            currentStudentId = currentStudent?.id ?: 1L,
            preselectedGroupId = addExpenseGroupId,
            onDismiss = {
                showAddExpenseDialog = false
                addExpenseGroupId = null
            },
            onConfirm = { groupId, title, amount, paidById, category, selectedMembers, splitType, customMap ->
                viewModel.addExpense(
                    groupId = groupId,
                    title = title,
                    amount = amount,
                    paidByStudentId = paidById,
                    category = category,
                    selectedMemberIds = selectedMembers,
                    splitType = splitType,
                    customAmounts = customMap
                )
            }
        )
    }

    // Create Group Dialog
    if (showCreateGroupDialog) {
        CreateGroupDialog(
            students = allStudents,
            currentStudentId = currentStudent?.id ?: 1L,
            onDismiss = { showCreateGroupDialog = false },
            onConfirm = { name, type, description, memberIds ->
                viewModel.createGroup(name, type, description, memberIds)
            }
        )
    }

    // Create Mess Bill Dialog
    if (showCreateMessBillDialog) {
        CreateMessBillDialog(
            groups = allGroups,
            students = allStudents,
            onDismiss = { showCreateMessBillDialog = false },
            onConfirm = { groupId, monthYear, totalAmount, totalDays, attendanceDays, notesMap ->
                viewModel.createMessBill(groupId, monthYear, totalAmount, totalDays, attendanceDays, notesMap)
            }
        )
    }

    // Settle Up Dialog
    if (showSettleUpDialog) {
        SettleUpDialog(
            students = allStudents,
            initialPayerId = settlePayerId,
            initialReceiverId = settleReceiverId,
            initialAmount = settleAmount,
            onDismiss = {
                showSettleUpDialog = false
                settleReceiverId = null
                settleAmount = null
            },
            onConfirm = { fromId, toId, amount, mode ->
                viewModel.settleDebt(fromId, toId, null, amount, mode)
            }
        )
    }
}
