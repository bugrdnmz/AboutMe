package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DailyNotesScreen(dailyNoteDao: DailyNoteDao, navController: NavController) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var noteContent by remember { mutableStateOf("") }
    var noteTitle by remember { mutableStateOf("") }

    // Dinamik tarih için Calendar kullanımı
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy", Locale("tr", "TR")) }
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    // Seçilen tarih için state
    var selectedDate by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    // Saat aralığı seçimi için
    var startHour by remember { mutableStateOf("08:00") }
    var endHour by remember { mutableStateOf("09:00") }
    var startHourMenuExpanded by remember { mutableStateOf(false) }
    var endHourMenuExpanded by remember { mutableStateOf(false) }
    val hours = remember {
        (0..23).flatMap { hour ->
            listOf("$hour:00", "$hour:30").map {
                if (hour < 10) "0$it" else it
            }
        }
    }

    // Günlük notları veritabanından al
    var dailyNotes by remember { mutableStateOf<List<DailyNote>>(emptyList()) }
    LaunchedEffect(selectedDate) {
        dailyNotes = dailyNoteDao.getNotesForDay(selectedDate)
    }

    // Yeni not eklemek için durum
    var isAddingNote by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp) // Bottom navigation için padding
                .verticalScroll(scrollState), // Aşağı kaydırma için
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Günlük Notlar",
                fontSize = 30.sp,
                color = Color(0xFFFB8C00),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Takvim görünümü - ay ve yıl için dinamik
            CalendarView(
                modifier = Modifier.fillMaxWidth(),
                month = currentMonth,
                year = currentYear,
                selectedDay = selectedDay,
                onDateSelected = { day ->
                    selectedDay = day
                    calendar.set(currentYear, currentMonth, day)
                    selectedDate = dateFormat.format(calendar.time)
                },
                onMonthChanged = { newMonth, newYear ->
                    currentMonth = newMonth
                    currentYear = newYear
                    selectedDay = -1 // Ay değiştiğinde seçili günü sıfırla
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Saat aralığı seçimi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Başlangıç saati
                Column {
                    Text(
                        text = "Başlangıç:",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box {
                        Button(
                            onClick = { startHourMenuExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00))
                        ) {
                            Text(startHour)
                        }

                        DropdownMenu(
                            expanded = startHourMenuExpanded,
                            onDismissRequest = { startHourMenuExpanded = false }
                        ) {
                            hours.forEach { hour ->
                                DropdownMenuItem(
                                    text = { Text(hour) },
                                    onClick = {
                                        startHour = hour
                                        startHourMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "-",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Bitiş saati
                Column {
                    Text(
                        text = "Bitiş:",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box {
                        Button(
                            onClick = { endHourMenuExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00))
                        ) {
                            Text(endHour)
                        }

                        DropdownMenu(
                            expanded = endHourMenuExpanded,
                            onDismissRequest = { endHourMenuExpanded = false }
                        ) {
                            hours.forEach { hour ->
                                DropdownMenuItem(
                                    text = { Text(hour) },
                                    onClick = {
                                        endHour = hour
                                        endHourMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Günlük notlar listesi
            Text(
                text = "Notlarım - $selectedDate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (dailyNotes.isEmpty()) {
                Text(
                    text = "Bu güne ait not bulunmuyor",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                // Not listesi
                dailyNotes.forEach { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = note.hour,
                                    fontSize = 12.sp,
                                    color = Color(0xFF2196F3),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = note.note,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // İçerik (extra_column) varsa göster
                                if (!note.extra_column.isNullOrEmpty()) {
                                    Text(
                                        text = note.extra_column,
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    dailyNoteDao.delete(note)
                                    dailyNotes = dailyNoteDao.getNotesForDay(selectedDate)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Not eklemek için UI
            if (isAddingNote) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Yeni Not Ekle",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        TextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            label = { Text("Başlık") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            label = { Text("Not İçeriği") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Notu kaydetme butonları
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { isAddingNote = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("İptal")
                            }

                            Button(
                                onClick = {
                                    if (noteTitle.isNotEmpty() && selectedDay > 0) {
                                        val timeRange = "$startHour - $endHour"
                                        val newNote = DailyNote(
                                            day = selectedDate,
                                            hour = timeRange,
                                            note = noteTitle,
                                            titleFont = "Arial",
                                            titleColor = Color.Black.toArgb(),
                                            titleWeight = "Bold",
                                            contentFont = "Arial",
                                            contentColor = Color.Black.toArgb(),
                                            contentWeight = "Normal",
                                            extra_column = noteContent // İçerik için extra_column'u kullanalım
                                        )

                                        coroutineScope.launch {
                                            dailyNoteDao.insert(newNote)
                                            // Notu kaydettikten sonra listeyi güncelle
                                            dailyNotes = dailyNoteDao.getNotesForDay(selectedDate)
                                            // Formu temizle
                                            noteTitle = ""
                                            noteContent = ""
                                            isAddingNote = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00))
                            ) {
                                Text("Notu Kaydet")
                            }
                        }
                    }
                }
            } else {
                // Not eklemek için "+" butonu
                Button(
                    onClick = { isAddingNote = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Note")
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Yeni Not Ekle")
                }
            }

            // Bottom padding to ensure the content doesn't hide behind the nav bar
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Bottom Navigation
        AppBottomNavigation(
            navController = navController,
            onAddClick = { isAddingNote = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    month: Int,
    year: Int,
    selectedDay: Int,
    onDateSelected: (Int) -> Unit,
    onMonthChanged: (Int, Int) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("tr", "TR")) }

    // Ay başını ayarla
    calendar.set(year, month, 1)

    // Ayın ilk gününün haftanın hangi günü olduğunu bul (0 = Pazar, 1 = Pazartesi)
    val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

    // Ayın kaç gün olduğunu bul
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(modifier = modifier.background(Color.White, shape = RoundedCornerShape(8.dp))) {
        // Ay ve yıl başlığı
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                var newMonth = month - 1
                var newYear = year
                if (newMonth < 0) {
                    newMonth = 11
                    newYear--
                }
                onMonthChanged(newMonth, newYear)
            }) {
                Icon(Icons.Default.KeyboardArrowLeft, "Previous Month")
            }

            calendar.set(year, month, 1)
            Text(
                text = monthFormat.format(calendar.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                var newMonth = month + 1
                var newYear = year
                if (newMonth > 11) {
                    newMonth = 0
                    newYear++
                }
                onMonthChanged(newMonth, newYear)
            }) {
                Icon(Icons.Default.KeyboardArrowRight, "Next Month")
            }
        }

        // Haftanın günleri - Türkçe
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
                    color = Color.Gray
                )
            }
        }

        // Takvim günleri
        // Pazartesi = 1, Salı = 2, ... hesaplaması için
        // Java Calendar'da Pazar = 1, Pazartesi = 2 şeklinde. Bunu düzeltmemiz gerekiyor.
        val startOffset = if (firstDayOfMonth == Calendar.SUNDAY) 6 else firstDayOfMonth - 2

        // Bugünün tarihini al
        val today = Calendar.getInstance()
        val isCurrentMonth = today.get(Calendar.MONTH) == month && today.get(Calendar.YEAR) == year
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        val totalDays = startOffset + daysInMonth
        val totalWeeks = (totalDays + 6) / 7 // Yukarı yuvarlama

        repeat(totalWeeks) { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dayOfWeek ->
                    val index = week * 7 + dayOfWeek
                    if (index < startOffset || index >= startOffset + daysInMonth) {
                        // Boş hücre
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val day = index - startOffset + 1
                        // Bugün mü veya seçili gün mü kontrol et
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
                                        isToday -> Color(0xFFFB8C00) // Bugünün rengi turuncu
                                        isSelected -> Color(0xFF81C784) // Seçili günün rengi yeşil
                                        else -> Color.LightGray.copy(alpha = 0.2f)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 14.sp,
                                color = when {
                                    isToday || isSelected -> Color.White
                                    else -> Color.Black
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}