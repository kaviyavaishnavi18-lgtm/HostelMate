package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseGroup
import com.example.data.models.SplitType
import com.example.data.models.Student
import com.example.ui.theme.CampusEmeraldPrimary
import com.example.ui.theme.CampusMintContainer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    groups: List<ExpenseGroup>,
    students: List<Student>,
    currentStudentId: Long,
    preselectedGroupId: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        groupId: Long,
        title: String,
        amount: Double,
        paidByStudentId: Long,
        category: ExpenseCategory,
        selectedMemberIds: List<Long>,
        splitType: SplitType,
        customAmounts: Map<Long, Double>
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedGroupId by remember {
        mutableStateOf(preselectedGroupId ?: groups.firstOrNull()?.id ?: 1L)
    }
    var paidById by remember { mutableStateOf(currentStudentId) }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var splitType by remember { mutableStateOf(SplitType.EQUAL) }

    // Pre-select all students for splitting by default
    val selectedMembers = remember {
        mutableStateMapOf<Long, Boolean>().apply {
            students.forEach { put(it.id, true) }
        }
    }
    val customAmounts = remember {
        mutableStateMapOf<Long, String>().apply {
            students.forEach { put(it.id, "") }
        }
    }

    var groupExpanded by remember { mutableStateOf(false) }
    var paidByExpanded by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val activeMemberCount = selectedMembers.filter { it.value }.size.coerceAtLeast(1)
    val equalShare = if (amount > 0) amount / activeMemberCount else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Shared Expense", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title (e.g. WiFi Bill)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Total Amount") },
                    leadingIcon = {
                        Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = CampusEmeraldPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Group selector
                Text("Select Group:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = groupExpanded,
                    onExpandedChange = { groupExpanded = !groupExpanded }
                ) {
                    val currentGroup = groups.find { it.id == selectedGroupId }
                    OutlinedTextField(
                        value = currentGroup?.name ?: "Select Group",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("expense_group_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = groupExpanded,
                        onDismissRequest = { groupExpanded = false }
                    ) {
                        groups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = {
                                    selectedGroupId = group.id
                                    groupExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Paid By
                Text("Paid By:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = paidByExpanded,
                    onExpandedChange = { paidByExpanded = !paidByExpanded }
                ) {
                    val currentPayer = students.find { it.id == paidById }
                    OutlinedTextField(
                        value = "${currentPayer?.name ?: "Select Payer"} (${currentPayer?.roomNumber ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paidByExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("expense_paidby_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = paidByExpanded,
                        onDismissRequest = { paidByExpanded = false }
                    ) {
                        students.forEach { student ->
                            DropdownMenuItem(
                                text = { Text("${student.name} (${student.roomNumber})") },
                                onClick = {
                                    paidById = student.id
                                    paidByExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                Text("Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ExpenseCategory.values().forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.name.replace("_", " ").lowercase().capitalize()) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Split Type Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Split Among:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row {
                        OutlinedButton(
                            onClick = { splitType = SplitType.EQUAL },
                            colors = if (splitType == SplitType.EQUAL) ButtonDefaults.outlinedButtonColors(containerColor = CampusMintContainer) else ButtonDefaults.outlinedButtonColors(),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Equal", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { splitType = SplitType.CUSTOM },
                            colors = if (splitType == SplitType.CUSTOM) ButtonDefaults.outlinedButtonColors(containerColor = CampusMintContainer) else ButtonDefaults.outlinedButtonColors(),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Custom", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Member checkboxes
                students.forEach { student ->
                    val isChecked = selectedMembers[student.id] ?: true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { selectedMembers[student.id] = it }
                        )
                        StudentAvatar(name = student.name, avatarColorHex = student.avatarColorHex, size = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = student.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (splitType == SplitType.EQUAL && isChecked) {
                            Text(
                                text = "₹%,.0f".format(equalShare),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = CampusEmeraldPrimary
                            )
                        } else if (splitType == SplitType.CUSTOM && isChecked) {
                            OutlinedTextField(
                                value = customAmounts[student.id] ?: "",
                                onValueChange = { customAmounts[student.id] = it },
                                placeholder = { Text("₹0") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(90.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && amount > 0) {
                        val activeMemberIds = selectedMembers.filter { it.value }.keys.toList()
                        val customMap = if (splitType == SplitType.CUSTOM) {
                            activeMemberIds.associateWith {
                                customAmounts[it]?.toDoubleOrNull() ?: 0.0
                            }
                        } else emptyMap()

                        onConfirm(
                            selectedGroupId,
                            title,
                            amount,
                            paidById,
                            selectedCategory,
                            activeMemberIds,
                            splitType,
                            customMap
                        )
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank() && amount > 0,
                modifier = Modifier.testTag("submit_expense_button")
            ) {
                Text("Add Expense")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
