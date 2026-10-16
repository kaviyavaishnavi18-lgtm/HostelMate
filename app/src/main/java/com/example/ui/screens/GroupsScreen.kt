package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.Expense
import com.example.data.models.ExpenseGroup
import com.example.data.models.GroupDetail
import com.example.data.models.Student
import com.example.ui.components.ArrowSquareButton
import com.example.ui.components.CategoryChip
import com.example.ui.components.GroupTypeBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.NeoDarkOutline
import com.example.ui.theme.NeoDarkText
import com.example.ui.theme.NeoMutedText
import com.example.ui.theme.PastelLilacBackground
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPink
import com.example.ui.theme.StatusNegativeRed
import com.example.ui.theme.StatusPositiveGreen
import com.example.ui.viewmodel.HostelMateViewModel

@Composable
fun GroupsScreen(
    viewModel: HostelMateViewModel,
    onOpenCreateGroup: () -> Unit,
    onOpenAddExpense: (groupId: Long) -> Unit
) {
    val context = LocalContext.current
    val groupDetails by viewModel.groupDetails.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()

    var selectedGroupId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        containerColor = PastelLilacBackground,
        floatingActionButton = {
            Surface(
                onClick = onOpenCreateGroup,
                shape = RoundedCornerShape(18.dp),
                color = NeoDarkOutline,
                border = BorderStroke(2.dp, NeoDarkOutline),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .padding(bottom = 76.dp)
                    .size(54.dp)
                    .testTag("fab_create_group")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Create Group", tint = Color.White)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hostel Groups",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = NeoDarkText
                        )
                        Text(
                            text = "Shared rooms, messes, trips & celebrations",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoMutedText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(groupDetails) { detail ->
                val isExpanded = selectedGroupId == detail.group.id
                val groupExpenses = allExpenses.filter { it.groupId == detail.group.id }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, NeoDarkOutline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("group_card_${detail.group.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    GroupTypeBadge(type = detail.group.type)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PastelPeach,
                                        border = BorderStroke(1.dp, NeoDarkOutline)
                                    ) {
                                        Text(
                                            text = detail.group.inviteCode,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = NeoDarkText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = detail.group.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = NeoDarkText
                                )
                                if (detail.group.description.isNotBlank()) {
                                    Text(
                                        text = detail.group.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeoMutedText
                                    )
                                }
                            }

                            Surface(
                                onClick = { selectedGroupId = if (isExpanded) null else detail.group.id },
                                shape = RoundedCornerShape(10.dp),
                                color = PastelLilacBackground,
                                border = BorderStroke(1.dp, NeoDarkOutline),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = NeoDarkText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Spending", fontSize = 11.sp, color = NeoMutedText)
                                Text("₹%,.0f".format(detail.totalExpenses), fontWeight = FontWeight.Black, fontSize = 15.sp, color = NeoDarkText)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Your Balance", fontSize = 11.sp, color = NeoMutedText)
                                Text(
                                    text = if (detail.myBalanceInGroup >= 0) "+₹%,.0f".format(detail.myBalanceInGroup)
                                    else "-₹%,.0f".format(kotlin.math.abs(detail.myBalanceInGroup)),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = if (detail.myBalanceInGroup >= 0) StatusPositiveGreen else StatusNegativeRed
                                )
                            }
                        }

                        // Expanded View with member rosters and expense history
                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                Divider(color = NeoDarkOutline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(12.dp))

                                // Invite Code Copy
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Invite Code: ${detail.group.inviteCode}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Group Code", detail.group.inviteCode)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Group code copied: ${detail.group.inviteCode}", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = PastelPeach,
                                        border = BorderStroke(1.dp, NeoDarkOutline)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Code", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Group Members (${detail.members.size}):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    detail.members.forEach { m ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            StudentAvatar(name = m.name, avatarColorHex = m.avatarColorHex, size = 32.dp)
                                            Text(m.name.substringBefore(" "), fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("Recent Expenses (${groupExpenses.size}):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(6.dp))
                                if (groupExpenses.isEmpty()) {
                                    Text("No expenses logged yet.", style = MaterialTheme.typography.bodySmall, color = NeoMutedText)
                                } else {
                                    val studentMap = allStudents.associateBy { it.id }
                                    groupExpenses.take(4).forEach { exp ->
                                        val payer = studentMap[exp.paidByStudentId]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CategoryChip(category = exp.category)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(exp.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("Paid by ${payer?.name ?: "Member"}", fontSize = 11.sp, color = NeoMutedText)
                                                }
                                            }
                                            Text("₹%,.0f".format(exp.amount), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onOpenAddExpense(detail.group.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeoDarkOutline),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Expense", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Surface(
                                        onClick = { viewModel.deleteGroup(detail.group) },
                                        shape = RoundedCornerShape(14.dp),
                                        color = PastelPink,
                                        border = BorderStroke(1.5.dp, NeoDarkOutline),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusNegativeRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
