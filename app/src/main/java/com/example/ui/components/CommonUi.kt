package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ExpenseCategory
import com.example.data.models.GroupType
import com.example.data.models.Student
import com.example.ui.theme.NeoDarkOutline
import com.example.ui.theme.NeoDarkText
import com.example.ui.theme.NeoMutedText
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.PastelLilacDark
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

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        PastelLilacDark
    }
}

@Composable
fun StudentAvatar(
    name: String,
    avatarColorHex: String,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val initials = name.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "S" }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.35f))
            .background(parseColor(avatarColorHex))
            .border(1.5.dp, NeoDarkOutline, RoundedCornerShape(size * 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.4).sp
        )
    }
}

@Composable
fun RupeeText(
    amount: Double,
    modifier: Modifier = Modifier,
    isCredit: Boolean? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val formatted = "₹%,.0f".format(kotlin.math.abs(amount))
    val prefix = when (isCredit) {
        true -> "+"
        false -> "-"
        null -> ""
    }
    val textColor = when (isCredit) {
        true -> StatusPositiveGreen
        false -> StatusNegativeRed
        null -> style.color
    }

    Text(
        text = "$prefix$formatted",
        style = style.copy(color = textColor, fontWeight = fontWeight),
        modifier = modifier
    )
}

@Composable
fun SquircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PastelPeach,
    tint: Color = NeoDarkText,
    size: Dp = 44.dp
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = BorderStroke(1.5.dp, NeoDarkOutline),
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ArrowSquareButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = NeoDarkOutline,
        border = BorderStroke(1.5.dp, NeoDarkOutline),
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.NorthEast,
                contentDescription = "Open",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(category: ExpenseCategory, modifier: Modifier = Modifier) {
    val (icon, label, bgColor) = when (category) {
        ExpenseCategory.FOOD -> Triple(Icons.Default.Restaurant, "Food", PastelPeach)
        ExpenseCategory.RENT_UTILITY -> Triple(Icons.Default.Lightbulb, "Utility", PastelMint)
        ExpenseCategory.GROCERY -> Triple(Icons.Default.ShoppingCart, "Grocery", PastelBlue)
        ExpenseCategory.TRIP -> Triple(Icons.Default.BeachAccess, "Trip", PastelPink)
        ExpenseCategory.PARTY -> Triple(Icons.Default.Cake, "Party", PastelLilacSurface)
        ExpenseCategory.OTHER -> Triple(Icons.Default.Receipt, "Other", Color.White)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, NeoDarkOutline),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = NeoDarkText, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, color = NeoDarkText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GroupTypeBadge(type: GroupType, modifier: Modifier = Modifier) {
    val (label, bgColor) = when (type) {
        GroupType.ROOM -> "Room Flat" to PastelMint
        GroupType.MESS -> "Mess Bill" to PastelPeach
        GroupType.TRIP -> "Trip Outing" to PastelBlue
        GroupType.PARTY -> "Party" to PastelPink
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, NeoDarkOutline),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = NeoDarkText,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(isPaid: Boolean, modifier: Modifier = Modifier) {
    val label = if (isPaid) "PAID" else "PENDING"
    val bgColor = if (isPaid) StatusPositiveContainer else StatusWarningContainer
    val textColor = if (isPaid) StatusPositiveGreen else StatusWarningOrange

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.5.dp, NeoDarkOutline),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// 4-Pill Quick Stats Row matching the reference image's (Weight 9Kg, Gender Male, Age 2year, Time 50min)
@Composable
fun FourStatPillsRow(
    stat1Label: String, stat1Val: String, stat1Bg: Color = PastelMint,
    stat2Label: String, stat2Val: String, stat2Bg: Color = PastelBlue,
    stat3Label: String, stat3Val: String, stat3Bg: Color = PastelPink,
    stat4Label: String, stat4Val: String, stat4Bg: Color = PastelPeach,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatPill(stat1Label, stat1Val, stat1Bg, Modifier.weight(1f))
        StatPill(stat2Label, stat2Val, stat2Bg, Modifier.weight(1f))
        StatPill(stat3Label, stat3Val, stat3Bg, Modifier.weight(1f))
        StatPill(stat4Label, stat4Val, stat4Bg, Modifier.weight(1f))
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, NeoDarkOutline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = NeoMutedText,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = NeoDarkText,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PersonSwitcherDialog(
    students: List<Student>,
    currentStudentId: Long,
    onSelectStudent: (Student) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SquircleIconButton(
                    icon = Icons.Default.SwapHoriz,
                    onClick = {},
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Switch Student Profile", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Select a roommate persona to view balances and pending dues:",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoMutedText
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn {
                    items(students) { student ->
                        val isSelected = student.id == currentStudentId
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PastelPeach else Color.White
                            ),
                            border = BorderStroke(1.5.dp, NeoDarkOutline),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSelectStudent(student)
                                    onDismiss()
                                }
                                .testTag("select_student_${student.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StudentAvatar(name = student.name, avatarColorHex = student.avatarColorHex)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoDarkText
                                    )
                                    Text(
                                        text = "${student.roomNumber} • ${student.upiId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeoMutedText
                                    )
                                }
                                if (isSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = NeoDarkOutline,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeoDarkOutline),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, NeoDarkOutline),
                modifier = Modifier.testTag("close_persona_switcher")
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}
