package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.models.ExpenseGroup
import com.example.data.models.Student
import com.example.ui.theme.CampusAmberContainer
import com.example.ui.theme.CampusEmeraldPrimary
import com.example.ui.theme.CampusMintContainer
import com.example.ui.theme.CampusOnMintContainer

@Composable
fun CreateMessBillDialog(
    groups: List<ExpenseGroup>,
    students: List<Student>,
    preselectedGroupId: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        groupId: Long,
        monthYear: String,
        totalAmount: Double,
        totalDays: Int,
        attendanceDays: Map<Long, Int>,
        notesMap: Map<Long, String>
    ) -> Unit
) {
    var monthYear by remember { mutableStateOf("November 2026") }
    var totalAmountText by remember { mutableStateOf("12000") }
    var totalDaysText by remember { mutableStateOf("30") }

    val messGroup = groups.find { it.type == com.example.data.models.GroupType.MESS }
        ?: groups.firstOrNull()
    val groupId = preselectedGroupId ?: messGroup?.id ?: 1L

    val attendanceDays = remember {
        mutableStateMapOf<Long, String>().apply {
            students.forEach { put(it.id, "30") }
        }
    }
    val attendanceNotes = remember {
        mutableStateMapOf<Long, String>().apply {
            students.forEach { put(it.id, "Full attendance") }
        }
    }

    val totalAmount = totalAmountText.toDoubleOrNull() ?: 0.0
    val totalDays = totalDaysText.toIntOrNull() ?: 30

    val intAttendanceMap = attendanceDays.mapValues { it.value.toIntOrNull() ?: 0 }
    val sumStudentDays = intAttendanceMap.values.sum().coerceAtLeast(1)
    val dailyRate = if (totalAmount > 0) totalAmount / sumStudentDays else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = CampusEmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Mess Bill Split", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Split monthly catering mess bills fairly based on the exact number of days each student ate at the mess.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = monthYear,
                    onValueChange = { monthYear = it },
                    label = { Text("Billing Month & Year") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mess_month_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = totalAmountText,
                        onValueChange = { totalAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Total Bill") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = CampusEmeraldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("mess_total_bill_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = totalDaysText,
                        onValueChange = { totalDaysText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Mess Days") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("mess_total_days_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rate calculation preview card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CampusMintContainer),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Smart Rate Calculation:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CampusOnMintContainer
                        )
                        Text(
                            text = "₹%,.0f total bill ÷ %d total student days = ₹%.1f/day per student".format(totalAmount, sumStudentDays, dailyRate),
                            style = MaterialTheme.typography.bodySmall,
                            color = CampusOnMintContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Student Mess Attendance (Days Attended):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                students.forEach { student ->
                    val days = intAttendanceMap[student.id] ?: 0
                    val studentShare = days * dailyRate

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StudentAvatar(name = student.name, avatarColorHex = student.avatarColorHex, size = 28.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("${student.roomNumber}", style = MaterialTheme.typography.bodySmall)
                                }
                                OutlinedTextField(
                                    value = attendanceDays[student.id] ?: "30",
                                    onValueChange = { attendanceDays[student.id] = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Days") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(76.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "₹%,.0f".format(studentShare),
                                    fontWeight = FontWeight.Bold,
                                    color = CampusEmeraldPrimary,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = attendanceNotes[student.id] ?: "",
                                onValueChange = { attendanceNotes[student.id] = it },
                                placeholder = { Text("Notes (e.g. absent 10 days)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (totalAmount > 0 && monthYear.isNotBlank()) {
                        val attendance = attendanceDays.mapValues { it.value.toIntOrNull() ?: 0 }
                        onConfirm(groupId, monthYear, totalAmount, totalDays, attendance, attendanceNotes)
                        onDismiss()
                    }
                },
                enabled = totalAmount > 0 && monthYear.isNotBlank(),
                modifier = Modifier.testTag("submit_create_mess_bill")
            ) {
                Text("Save Mess Bill")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
