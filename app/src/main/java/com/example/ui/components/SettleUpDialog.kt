package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.models.Student
import com.example.ui.theme.CampusEmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpDialog(
    students: List<Student>,
    initialPayerId: Long,
    initialReceiverId: Long? = null,
    initialAmount: Double? = null,
    onDismiss: () -> Unit,
    onConfirm: (fromStudentId: Long, toStudentId: Long, amount: Double, paymentMode: String) -> Unit
) {
    var fromStudentId by remember { mutableStateOf(initialPayerId) }
    var toStudentId by remember {
        mutableStateOf(initialReceiverId ?: students.find { it.id != initialPayerId }?.id ?: 2L)
    }
    var amountText by remember {
        mutableStateOf(if (initialAmount != null && initialAmount > 0) "%.0f".format(initialAmount) else "")
    }
    var paymentMode by remember { mutableStateOf("UPI") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = CampusEmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Payment & Settle", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Record a direct settlement between roommates to balance dues:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Payer Dropdown
                Text("Who Paid:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = fromExpanded,
                    onExpandedChange = { fromExpanded = !fromExpanded }
                ) {
                    val payer = students.find { it.id == fromStudentId }
                    OutlinedTextField(
                        value = "${payer?.name ?: ""} (${payer?.roomNumber ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = fromExpanded,
                        onDismissRequest = { fromExpanded = false }
                    ) {
                        students.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.name} (${s.roomNumber})") },
                                onClick = {
                                    fromStudentId = s.id
                                    fromExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Receiver Dropdown
                Text("Paid To:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = toExpanded,
                    onExpandedChange = { toExpanded = !toExpanded }
                ) {
                    val receiver = students.find { it.id == toStudentId }
                    OutlinedTextField(
                        value = "${receiver?.name ?: ""} (${receiver?.upiId ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = toExpanded,
                        onDismissRequest = { toExpanded = false }
                    ) {
                        students.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.name} (${s.upiId})") },
                                onClick = {
                                    toStudentId = s.id
                                    toExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Amount Settled") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = CampusEmeraldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settle_amount_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Payment Mode:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    FilterChip(
                        selected = paymentMode == "UPI",
                        onClick = { paymentMode = "UPI" },
                        label = { Text("UPI / GPay / PhonePe") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = paymentMode == "CASH",
                        onClick = { paymentMode = "CASH" },
                        label = { Text("Cash") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (amount > 0 && fromStudentId != toStudentId) {
                        onConfirm(fromStudentId, toStudentId, amount, paymentMode)
                        onDismiss()
                    }
                },
                enabled = amount > 0 && fromStudentId != toStudentId,
                modifier = Modifier.testTag("confirm_settlement_button")
            ) {
                Text("Confirm Settlement")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
