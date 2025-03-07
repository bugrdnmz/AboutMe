/*package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun CustomCalendarView(
    modifier: Modifier = Modifier,
    month: Int,
    year: Int,
    selectedDay: Int,
    onDateSelected: (Int) -> Unit,
    onMonthChanged: (Int, Int) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("tr", "TR")) }

    // Set the beginning of the month
    calendar.set(year, month, 1)

    // Find the first day of the month (0 = Sunday, 1 = Monday)
    val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

    // Find the number of days in the month
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TerraCotta.copy(alpha = 0.8f),
                        TerraCotta.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(12.dp)
    ) {
        // Month and year heading
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    var newMonth = month - 1
                    var newYear = year
                    if (newMonth < 0) {
                        newMonth = 11
                        newYear--
                    }
                    onMonthChanged(newMonth, newYear)
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = LightGray
                )
            }

            calendar.set(year, month, 1)
            Text(
                text = monthFormat.format(calendar.time).uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SandyBrown
            )

            IconButton(
                onClick = {
                    var newMonth = month + 1
                    var newYear = year
                    if (newMonth > 11) {
                        newMonth = 0
                        newYear++
                    }
                    onMonthChanged(newMonth, newYear)
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = LightGray
                )
            }
        }

        // Days of the week - Turkish
        Row(modifier = Modifier.fillMaxWidth()) {
            val days = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = LightGray.copy(alpha = 0.7f)
                )
            }
        }

        // Calendar days
        // For Monday = 1, Tuesday = 2, ... calculations
        // In Java Calendar Sunday = 1, Monday = 2 etc. We need to correct this.
        val startOffset = if (firstDayOfMonth == Calendar.SUNDAY) 6 else firstDayOfMonth - 2

        // Get today's date
        val today = Calendar.getInstance()
        val isCurrentMonth = today.get(Calendar.MONTH) == month && today.get(Calendar.YEAR) == year
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        val totalDays = startOffset + daysInMonth
        val totalWeeks = (totalDays + 6) / 7 // Round up

        repeat(totalWeeks) { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dayOfWeek ->
                    val index = week * 7 + dayOfWeek
                    if (index < startOffset || index >= startOffset + daysInMonth) {
                        // Empty cell
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                        )
                    } else {
                        val day = index - startOffset + 1
                        // Check if today or selected day
                        val isToday = isCurrentMonth && day == currentDay
                        val isSelected = day == selectedDay && !isToday

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clickable { onDateSelected(day) }
                                .background(
                                    when {
                                        isToday -> TerraCotta // Today's color terra cotta
                                        isSelected -> SandyBrown.copy(alpha = 0.7f) // Selected day sandy brown
                                        else -> Color.White.copy(alpha = 0.08f) // Subtle background for other days
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 15.sp,
                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isToday || isSelected -> LightGray
                                    else -> LightGray.copy(alpha = 0.9f)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}*/