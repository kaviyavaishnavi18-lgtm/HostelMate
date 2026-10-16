package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.MessAttendance
import com.example.data.models.MessBill
import com.example.data.models.Student
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.NeoDarkOutline
import com.example.ui.theme.NeoDarkText
import com.example.ui.theme.NeoMutedText
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.PastelLilacBackground
import com.example.ui.theme.PastelLilacSurface
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPink
import com.example.ui.theme.StatusNegativeContainer
import com.example.ui.theme.StatusNegativeRed
import com.example.ui.theme.StatusPositiveContainer
import com.example.ui.theme.StatusPositiveGreen
import com.example.ui.theme.StatusWarningContainer
import com.example.ui.theme.StatusWarningOrange
import com.example.ui.viewmodel.HostelMateViewModel

@Composable
fun MessSplitterScreen(
    viewModel: HostelMateViewModel,
    onOpenCreateMessBill: () -> Unit
) {
    val context = LocalContext.current
    val messBills by viewModel.allMessBills.collectAsStateWithLifecycle()
    val allAttendances by viewModel.allAttendances.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()

    val studentMap = allStudents.associateBy { it.id }

    Scaffold(
        containerColor = PastelLilacBackground,
        floatingActionButton = {
            Surface(
                onClick = onOpenCreateMessBill,
                shape = RoundedCornerShape(18.dp),
                color = PastelPeach,
                border = BorderStroke(2.dp, NeoDarkOutline),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .padding(bottom = 76.dp)
                    .size(54.dp)
                    .testTag("fab_create_mess_bill")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "New Mess Bill", tint = NeoDarkText)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Smart Mess Splitter",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = NeoDarkText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🍛", fontSize = 24.sp)
                        }
                        Text(
                            text = "Automatic absent-day meal deductions & daily rate math",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoMutedText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (messBills.isEmpty()) {
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
                            Icon(
                                Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = NeoDarkText,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Mess Bills Logged", fontWeight = FontWeight.Bold, color = NeoDarkText)
                            Text(
                                "Tap '+' below to add your monthly catering mess bill and split fairly by attendance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoMutedText
                            )
                        }
                    }
                }
            }

            items(messBills) { bill ->
                val attendances = allAttendances.filter { it.messBillId == bill.id }
                val paidCount = attendances.count { it.isPaid }
                val pendingCount = attendances.size - paidCount
                val pendingTotal = attendances.filter { !it.isPaid }.sumOf { it.calculatedAmount }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, NeoDarkOutline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("mess_bill_card_${bill.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PastelPeach,
                                    border = BorderStroke(1.5.dp, NeoDarkOutline),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = NeoDarkText,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = bill.monthYear,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = NeoDarkText
                                    )
                                    Text(
                                        text = "${bill.totalDays} days in cycle • ₹%.1f/day".format(bill.dailyRate),
                                        fontSize = 11.sp,
                                        color = NeoMutedText
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹%,.0f".format(bill.totalAmount),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = NeoDarkText
                                )
                                Text(
                                    text = "Total Bill",
                                    fontSize = 11.sp,
                                    color = NeoMutedText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status overview bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PastelMint,
                                border = BorderStroke(1.5.dp, NeoDarkOutline),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusPositiveGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("$paidCount Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeoDarkText)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PastelPink,
                                border = BorderStroke(1.5.dp, NeoDarkOutline),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = StatusNegativeRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("$pendingCount Pending (₹%,.0f)".format(pendingTotal), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeoDarkText)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = NeoDarkOutline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Attendance & Fair Share Breakdown:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = NeoDarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Attendance rows
                        attendances.forEach { att ->
                            val student = studentMap[att.studentId]
                            val absentDays = (bill.totalDays - att.daysAttended).coerceAtLeast(0)

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (att.isPaid) PastelMint.copy(alpha = 0.5f) else PastelPeach.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, NeoDarkOutline),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StudentAvatar(
                                        name = student?.name ?: "Student",
                                        avatarColorHex = student?.avatarColorHex ?: "#10B981",
                                        size = 32.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student?.name ?: "Student",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = NeoDarkText
                                        )
                                        Text(
                                            text = "${att.daysAttended} days attended • ${absentDays} days absent",
                                            fontSize = 11.sp,
                                            color = NeoMutedText
                                        )
                                        if (att.notes.isNotBlank()) {
                                            Text(
                                                text = att.notes,
                                                fontSize = 10.sp,
                                                color = NeoDarkText,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹%,.0f".format(att.calculatedAmount),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = NeoDarkText
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (att.isPaid) PastelMint else PastelPeach,
                                            border = BorderStroke(1.dp, NeoDarkOutline),
                                            modifier = Modifier
                                                .clickable { viewModel.toggleMessPaid(att.id, att.isPaid) }
                                                .testTag("toggle_mess_paid_${att.id}")
                                        ) {
                                            Text(
                                                text = if (att.isPaid) "PAID ✓" else "PENDING ⟳",
                                                color = NeoDarkText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // WhatsApp Reminder Generator Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val pendingList = attendances.filter { !it.isPaid }.mapNotNull { a ->
                                        val s = studentMap[a.studentId]
                                        if (s != null) "${s.name}: ₹%,.0f (${a.daysAttended} days)".format(a.calculatedAmount) else null
                                    }.joinToString("\n• ")

                                    val message = """
                                        📢 *HostelMate Mess Bill Notice* (${bill.monthYear})
                                        Total Bill: ₹%,.0f (Rate: ₹%.1f/day)
                                        
                                        *Pending Payments:*
                                        • $pendingList
                                        
                                        Please clear your dues via UPI to ${currentStudent?.upiId ?: "hostel-mess@upi"}. Thank you!
                                    """.trimIndent().format(bill.totalAmount, bill.dailyRate)

                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Mess Notice", message)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "WhatsApp reminder copied to clipboard!", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeoDarkOutline),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, NeoDarkOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("copy_whatsapp_reminder_${bill.id}")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy WhatsApp Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Surface(
                                onClick = { viewModel.deleteMessBill(bill) },
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
