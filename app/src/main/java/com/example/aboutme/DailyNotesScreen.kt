package com.example.aboutme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyNotesScreen(
    dailyNoteDao: DailyNoteDao,
    navController: NavController,
    sessionManager: SessionManager,
    isAddingDailyNote: Boolean = false,
    onAddingNoteComplete: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var noteContent by remember { mutableStateOf("") }
    var noteTitle by remember { mutableStateOf("") }

    val currentUserId = sessionManager.getUserId() ?: ""
    val username = sessionManager.getUsername() ?: "Kullanıcı"

    // Dinamik takvim durumu
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy", Locale("tr", "TR")) }
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    // Seçili tarih durumu
    var selectedDate by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    // Zaman aralığı seçimi
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

    // Görev kategorisi ve bildirim durumları
    var selectedCategory by remember { mutableStateOf(TaskCategory.OTHER) }
    var hasNotification by remember { mutableStateOf(false) }
    var notificationTime by remember { mutableStateOf<String?>(null) }
    var hasLocationAlert by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf<String?>(null) }

    // Dialog durumları
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    // Yeni not ekleme durumu
    var isAddingNote by remember { mutableStateOf(false) }

    // MainApp'ten isAddingDailyNote değiştiğinde isAddingNote'u güncelle
    LaunchedEffect(isAddingDailyNote) {
        if (isAddingDailyNote) {
            isAddingNote = true
        }
    }

    LaunchedEffect(Unit) {
        if (!sessionManager.isLoggedIn()) {
            navController.navigate("login_screen") {
                popUpTo("dailyNotes") { inclusive = true }
            }
        }
    }

    var dailyNotes by remember { mutableStateOf<List<EnhancedDailyNote>>(emptyList()) }
    LaunchedEffect(selectedDate, currentUserId) {
        if (currentUserId.isNotEmpty()) {
            // Veritabanı varlıklarını EnhancedDailyNote'a dönüştür
            val rawNotes = dailyNoteDao.getNotesForDayAndUser(selectedDate, currentUserId)
            dailyNotes = rawNotes.map { note ->
                EnhancedDailyNote(
                    id = note.id,
                    userId = note.userId,
                    day = note.day,
                    hour = note.hour,
                    note = note.note,
                    titleFont = note.titleFont,
                    titleColor = note.titleColor,
                    titleWeight = note.titleWeight,
                    contentFont = note.contentFont,
                    contentColor = note.contentColor,
                    contentWeight = note.contentWeight,
                    extra_column = note.extra_column,
                    // Bildirim ve konum alanlarını doğru şekilde eşleştir
                    isCompleted = note.isCompleted,
                    category = try {
                        TaskCategory.valueOf(note.category)
                    } catch (e: Exception) {
                        TaskCategory.OTHER
                    },
                    hasNotification = note.hasNotification,
                    notificationTime = note.notificationTime,
                    hasLocationAlert = note.hasLocationAlert,
                    locationName = note.locationName
                )
            }
        } else {
            dailyNotes = emptyList()
        }
    }

    // Not tamamlama durumunu güncelleyen işlev
    fun updateNoteCompletionStatus(note: EnhancedDailyNote, isCompleted: Boolean) {
        coroutineScope.launch {
            // Veritabanından orijinal notu al
            val dbNote = dailyNoteDao.getNoteById(note.id)
            if (dbNote != null) {
                // Yalnızca isCompleted alanını değiştir, diğer alanları kopyala
                val updatedNote = dbNote.copy(
                    isCompleted = isCompleted
                )
                // Veritabanında güncelle
                dailyNoteDao.update(updatedNote)

                // UI'ı güncelle
                dailyNotes = dailyNotes.map {
                    if (it.id == note.id) it.copy(isCompleted = isCompleted) else it
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SoftCream,                  // Arka plan için SoftCream
                        DeepBrown.copy(alpha = 0.1f) // Alt kısım için hafif DeepBrown tonu
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Kullanıcı bilgisiyle başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Günlük Notlar",
                    fontSize = 30.sp,
                    color = BurntOrange,  // Başlık için BurntOrange
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = username,
                        fontSize = 16.sp,
                        color = DeepBrown,  // Kullanıcı adı için DeepBrown
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(RustyRed, CircleShape)  // Kullanıcı avatarı için RustyRed
                            .clickable {
                                sessionManager.logout()
                                navController.navigate("login_screen") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.firstOrNull()?.toString()?.uppercase() ?: "?",
                            color = SoftCream,  // Avatar metni için SoftCream
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Takvim görünümü
            CustomCalendarView(
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
                    selectedDay = -1 // Ay değişiminde seçili günü sıfırla
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            // Zaman aralığı seçim kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DeepBrown.copy(alpha = 0.1f)  // Kart arkaplanı için hafif DeepBrown
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Başlangıç zamanı
                    Column {
                        Text(
                            text = "Başlangıç:",
                            fontSize = 16.sp,
                            color = DeepBrown,  // Metin için DeepBrown
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box {
                            Button(
                                onClick = { startHourMenuExpanded = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurntOrange  // Buton için BurntOrange
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(startHour, color = SoftCream)  // Buton metni için SoftCream
                            }

                            DropdownMenu(
                                expanded = startHourMenuExpanded,
                                onDismissRequest = { startHourMenuExpanded = false }
                            ) {
                                hours.forEach { hour ->
                                    DropdownMenuItem(
                                        text = { Text(hour, color = DeepBrown) },  // Açılır menü metni için DeepBrown
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
                        fontWeight = FontWeight.Bold,
                        color = WarmBeige  // Ayırıcı için WarmBeige
                    )

                    // Bitiş zamanı
                    Column {
                        Text(
                            text = "Bitiş:",
                            fontSize = 16.sp,
                            color = DeepBrown,  // Metin için DeepBrown
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box {
                            Button(
                                onClick = { endHourMenuExpanded = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurntOrange  // Buton için BurntOrange
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(endHour, color = SoftCream)  // Buton metni için SoftCream
                            }

                            DropdownMenu(
                                expanded = endHourMenuExpanded,
                                onDismissRequest = { endHourMenuExpanded = false }
                            ) {
                                hours.forEach { hour ->
                                    DropdownMenuItem(
                                        text = { Text(hour, color = DeepBrown) },  // Açılır menü metni için DeepBrown
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notları kaydetme butonu
            Button(
                onClick = {
                    if (noteTitle.isNotEmpty() && selectedDay > 0 && currentUserId.isNotEmpty()) {
                        val timeRange = "$startHour - $endHour"

                        // TÜM alanları düzgün şekilde ayarlanmış yeni not oluştur
                        val newNote = DailyNote(
                            userId = currentUserId,
                            day = selectedDate,
                            hour = timeRange,
                            note = noteTitle,
                            titleFont = "Default",
                            titleColor = DeepBrown.toArgb(),  // Başlık rengi için DeepBrown
                            titleWeight = "Bold",
                            contentFont = "Default",
                            contentColor = DeepBrown.copy(alpha = 0.7f).toArgb(),  // İçerik rengi için hafif DeepBrown
                            contentWeight = "Normal",
                            extra_column = noteContent,
                            // Bildirim ve konum alanlarını dahil et
                            isCompleted = false,
                            category = selectedCategory.name,
                            hasNotification = hasNotification,
                            notificationTime = notificationTime,
                            hasLocationAlert = hasLocationAlert,
                            locationName = locationName
                        )

                        coroutineScope.launch {
                            // Veritabanına ekle
                            dailyNoteDao.insert(newNote)

                            // Veritabanından listeyi yenile
                            val rawNotes = dailyNoteDao.getNotesForDayAndUser(selectedDate, currentUserId)
                            dailyNotes = rawNotes.map { note ->
                                EnhancedDailyNote(
                                    id = note.id,
                                    userId = note.userId,
                                    day = note.day,
                                    hour = note.hour,
                                    note = note.note,
                                    titleFont = note.titleFont,
                                    titleColor = note.titleColor,
                                    titleWeight = note.titleWeight,
                                    contentFont = note.contentFont,
                                    contentColor = note.contentColor,
                                    contentWeight = note.contentWeight,
                                    extra_column = note.extra_column,
                                    // Bildirim ve konum alanlarını doğru şekilde eşleştir
                                    isCompleted = note.isCompleted,
                                    category = try {
                                        TaskCategory.valueOf(note.category)
                                    } catch (e: Exception) {
                                        TaskCategory.OTHER
                                    },
                                    hasNotification = note.hasNotification,
                                    notificationTime = note.notificationTime,
                                    hasLocationAlert = note.hasLocationAlert,
                                    locationName = note.locationName
                                )
                            }

                            // Form değerlerini sıfırla
                            noteTitle = ""
                            noteContent = ""
                            selectedCategory = TaskCategory.OTHER
                            hasNotification = false
                            notificationTime = null
                            hasLocationAlert = false
                            locationName = null
                            isAddingNote = false
                            onAddingNoteComplete()  // MainApp'i bilgilendir
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RustyRed),  // Buton için RustyRed
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Notu Kaydet", color = SoftCream)  // Buton metni için SoftCream
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarihli notlar başlığı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notlarım",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BurntOrange  // Başlık için BurntOrange
                )

                Text(
                    text = selectedDate,
                    fontSize = 16.sp,
                    color = DeepBrown,  // Tarih için DeepBrown
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Divider(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                color = WarmBeige.copy(alpha = 0.3f),  // Ayırıcı için hafif WarmBeige
                thickness = 1.dp
            )

            // Notlar listesi
            if (dailyNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bu güne ait not bulunmuyor\nYeni not eklemek için aşağıdaki + butonuna tıklayın",
                        color = DeepBrown.copy(alpha = 0.6f),  // Metin için hafif DeepBrown
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            } else {
                // Kategoriye göre gruplandırılmış notlar listesi
                val groupedNotes = dailyNotes.groupBy { it.category }

                groupedNotes.forEach { (category, notes) ->
                    // Kategori başlığı
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(category.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBrown  // Kategori başlığı için DeepBrown
                        )
                    }

                    // Bu kategorideki notlar
                    notes.forEach { note ->
                        EnhancedNoteCard(
                            note = note,
                            onDelete = {
                                coroutineScope.launch {
                                    // Gerçek uygulamada DAO'nuzu kullanırdınız
                                    dailyNoteDao.delete(DailyNote(
                                        id = note.id,
                                        userId = note.userId,
                                        day = note.day,
                                        hour = note.hour,
                                        note = note.note,
                                        titleFont = note.titleFont.toString(),
                                        titleColor = note.titleColor,
                                        titleWeight = note.titleWeight.toString(),
                                        contentFont = note.contentFont.toString(),
                                        contentColor = note.contentColor,
                                        contentWeight = note.contentWeight.toString(),
                                        extra_column = note.extra_column,
                                        // Eksik alanları ekleyin
                                        isCompleted = note.isCompleted,
                                        category = note.category.name,
                                        hasNotification = note.hasNotification,
                                        notificationTime = note.notificationTime,
                                        hasLocationAlert = note.hasLocationAlert,
                                        locationName = note.locationName
                                    ))

                                    // Listeyi yenile
                                    val rawNotes = dailyNoteDao.getNotesForDayAndUser(selectedDate, currentUserId)
                                    dailyNotes = rawNotes.map { dbNote ->
                                        EnhancedDailyNote(
                                            id = dbNote.id,
                                            userId = dbNote.userId,
                                            day = dbNote.day,
                                            hour = dbNote.hour,
                                            note = dbNote.note,
                                            titleFont = dbNote.titleFont,
                                            titleColor = dbNote.titleColor,
                                            titleWeight = dbNote.titleWeight,
                                            contentFont = dbNote.contentFont,
                                            contentColor = dbNote.contentColor,
                                            contentWeight = dbNote.contentWeight,
                                            extra_column = dbNote.extra_column,
                                            // Yeni alanlar için varsayılan değerler
                                            isCompleted = dbNote.isCompleted,
                                            category = try {
                                                TaskCategory.valueOf(dbNote.category)
                                            } catch (e: Exception) {
                                                TaskCategory.OTHER
                                            },
                                            hasNotification = dbNote.hasNotification,
                                            notificationTime = dbNote.notificationTime,
                                            hasLocationAlert = dbNote.hasLocationAlert,
                                            locationName = dbNote.locationName
                                        )
                                    }
                                }
                            },
                            onToggleComplete = { isCompleted ->
                                updateNoteCompletionStatus(note, isCompleted)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Animasyonlu yeni not UI'ı
            AnimatedVisibility(
                visible = isAddingNote,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WarmBeige.copy(alpha = 0.2f)  // Kart arkaplanı için hafif WarmBeige
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Yeni Not Ekle",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BurntOrange  // Başlık için BurntOrange
                            )

                            IconButton(
                                onClick = {
                                    isAddingNote = false
                                    onAddingNoteComplete()  // MainApp'i bilgilendir
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DeepBrown.copy(alpha = 0.1f), CircleShape)  // Buton arkaplanı için hafif DeepBrown
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = DeepBrown,  // İkon için DeepBrown
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            label = { Text("Başlık", color = DeepBrown.copy(alpha = 0.8f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BurntOrange,
                                unfocusedBorderColor = DeepBrown.copy(alpha = 0.5f),
                                focusedTextColor = DeepBrown,
                                unfocusedTextColor = DeepBrown,
                                cursorColor = BurntOrange
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            label = { Text("Not İçeriği", color = DeepBrown.copy(alpha = 0.8f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BurntOrange,
                                unfocusedBorderColor = DeepBrown.copy(alpha = 0.5f),
                                focusedTextColor = DeepBrown,
                                unfocusedTextColor = DeepBrown,
                                cursorColor = BurntOrange
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Kategori seçimi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kategori:",
                                fontSize = 16.sp,
                                color = DeepBrown,
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            Box {
                                Button(
                                    onClick = { categoryMenuExpanded = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = selectedCategory.color
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(selectedCategory.label, color = SoftCream)
                                }

                                DropdownMenu(
                                    expanded = categoryMenuExpanded,
                                    onDismissRequest = { categoryMenuExpanded = false }
                                ) {
                                    TaskCategory.values().forEach { category ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .background(category.color, CircleShape)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(category.label)
                                                }
                                            },
                                            onClick = {
                                                selectedCategory = category
                                                categoryMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bildirim seçenekleri
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = hasNotification,
                                onCheckedChange = { hasNotification = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = RustyRed,
                                    uncheckedColor = DeepBrown.copy(alpha = 0.6f)
                                )
                            )

                            Text(
                                text = "Bildirim Ekle",
                                fontSize = 16.sp,
                                color = DeepBrown,
                                modifier = Modifier.clickable { hasNotification = !hasNotification }
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (hasNotification) {
                                Button(
                                    onClick = { showNotificationDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RustyRed.copy(alpha = 0.7f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Set Notification Time",
                                        tint = SoftCream
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = notificationTime ?: "Zaman Seç",
                                        color = SoftCream
                                    )
                                }
                            }
                        }

                        // Konum uyarısı seçeneği
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = hasLocationAlert,
                                onCheckedChange = { hasLocationAlert = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = RustyRed,
                                    uncheckedColor = DeepBrown.copy(alpha = 0.6f)
                                )
                            )

                            Text(
                                text = "Konum Hatırlatıcısı",
                                fontSize = 16.sp,
                                color = DeepBrown,
                                modifier = Modifier.clickable { hasLocationAlert = !hasLocationAlert }
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (hasLocationAlert) {
                                Button(
                                    onClick = { showLocationDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RustyRed.copy(alpha = 0.7f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = "Set Location",
                                        tint = SoftCream
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = locationName ?: "Konum Seç",
                                        color = SoftCream
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (noteTitle.isNotEmpty() && selectedDay > 0 && currentUserId.isNotEmpty()) {
                                    val timeRange = "$startHour - $endHour"

                                    val newNote = DailyNote(
                                        userId = currentUserId,
                                        day = selectedDate,
                                        hour = timeRange,
                                        note = noteTitle,
                                        titleFont = "Default",
                                        titleColor = DeepBrown.toArgb(),
                                        titleWeight = "Bold",
                                        contentFont = "Default",
                                        contentColor = DeepBrown.copy(alpha = 0.7f).toArgb(),
                                        contentWeight = "Normal",
                                        extra_column = noteContent,
                                        // Yeni alanları ekliyoruz
                                        isCompleted = false,
                                        category = selectedCategory.name,
                                        hasNotification = hasNotification,
                                        notificationTime = notificationTime,
                                        hasLocationAlert = hasLocationAlert,
                                        locationName = locationName
                                    )

                                    coroutineScope.launch {
                                        dailyNoteDao.insert(newNote)

                                        // DB'den çekilen notları EnhancedDailyNote'a dönüştürmek için
                                        val rawNotes = dailyNoteDao.getNotesForDayAndUser(selectedDate, currentUserId)
                                        val lastNote = rawNotes.maxByOrNull { it.id }

                                        dailyNotes = rawNotes.map { note ->
                                            EnhancedDailyNote(
                                                id = note.id,
                                                userId = note.userId,
                                                day = note.day,
                                                hour = note.hour,
                                                note = note.note,
                                                titleFont = note.titleFont,
                                                titleColor = note.titleColor,
                                                titleWeight = note.titleWeight,
                                                contentFont = note.contentFont,
                                                contentColor = note.contentColor,
                                                contentWeight = note.contentWeight,
                                                extra_column = note.extra_column,
                                                // DB'deki yeni alanları kullan
                                                isCompleted = note.isCompleted,
                                                category = try {
                                                    TaskCategory.valueOf(note.category)
                                                } catch (e: Exception) {
                                                    TaskCategory.OTHER
                                                },
                                                hasNotification = note.hasNotification,
                                                notificationTime = note.notificationTime,
                                                hasLocationAlert = note.hasLocationAlert,
                                                locationName = note.locationName
                                            )
                                        }

                                        // Form değerlerini sıfırla
                                        noteTitle = ""
                                        noteContent = ""
                                        selectedCategory = TaskCategory.OTHER
                                        hasNotification = false
                                        notificationTime = null
                                        hasLocationAlert = false
                                        locationName = null
                                        isAddingNote = false
                                        onAddingNoteComplete()  // MainApp'i bilgilendir
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RustyRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Notu Kaydet", color = SoftCream)
                        }
                    }
                }
            }

            // Not ekleme butonu (ekleme yapılmadığında görünür)
            if (!isAddingNote) {
                Button(
                    onClick = { isAddingNote = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BurntOrange
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Note",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("Yeni Not Ekle", fontSize = 16.sp, color = SoftCream)
                }
            }

            // Alt boşluk
            Spacer(modifier = Modifier.height(80.dp))
        }
        // Zaman bildirim dialoğu
        if (showNotificationDialog) {
            var tempHour by remember { mutableStateOf("15") }
            var tempMinute by remember { mutableStateOf("30") }
            var hourMenuExpanded by remember { mutableStateOf(false) }
            var minuteMenuExpanded by remember { mutableStateOf(false) }

            Dialog(onDismissRequest = { showNotificationDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepBrown),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bildirim Zamanı",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurntOrange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // İşlevsel açılır menülerle zaman seçici
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                Button(
                                    onClick = { hourMenuExpanded = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RustyRed
                                    )
                                ) {
                                    Text(tempHour, color = SoftCream)
                                }

                                DropdownMenu(
                                    expanded = hourMenuExpanded,
                                    onDismissRequest = { hourMenuExpanded = false }
                                ) {
                                    (0..23).forEach { hour ->
                                        val formattedHour = if (hour < 10) "0$hour" else "$hour"
                                        DropdownMenuItem(
                                            text = { Text(formattedHour) },
                                            onClick = {
                                                tempHour = formattedHour
                                                hourMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Text(
                                text = ":",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftCream
                            )

                            Box {
                                Button(
                                    onClick = { minuteMenuExpanded = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RustyRed
                                    )
                                ) {
                                    Text(tempMinute, color = SoftCream)
                                }

                                DropdownMenu(
                                    expanded = minuteMenuExpanded,
                                    onDismissRequest = { minuteMenuExpanded = false }
                                ) {
                                    listOf("00", "15", "30", "45").forEach { minute ->
                                        DropdownMenuItem(
                                            text = { Text(minute) },
                                            onClick = {
                                                tempMinute = minute
                                                minuteMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Butonlar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { showNotificationDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WarmBeige
                                )
                            ) {
                                Text("İptal", color = SoftCream)
                            }

                            Button(
                                onClick = {
                                    notificationTime = "$tempHour:$tempMinute"
                                    showNotificationDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurntOrange
                                )
                            ) {
                                Text("Kaydet", color = SoftCream)
                            }
                        }
                    }
                }
            }
        }

        // Konum seçim dialoğu
        if (showLocationDialog) {
            Dialog(onDismissRequest = { showLocationDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepBrown),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Konum Seçimi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurntOrange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Konum girişi
                        OutlinedTextField(
                            value = locationName ?: "",
                            onValueChange = { locationName = it },
                            label = { Text("Konum Adı", color = SoftCream.copy(alpha = 0.8f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BurntOrange,
                                unfocusedBorderColor = SoftCream.copy(alpha = 0.5f),
                                focusedTextColor = SoftCream,
                                unfocusedTextColor = SoftCream,
                                cursorColor = BurntOrange
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Konum açıklaması veya notu
                        Text(
                            text = "Not: Gerçek bir uygulama için harita entegrasyonu eklenebilir.",
                            fontSize = 14.sp,
                            color = SoftCream.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Butonlar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { showLocationDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WarmBeige
                                )
                            ) {
                                Text("İptal", color = SoftCream)
                            }

                            Button(
                                onClick = {
                                    // Konum adını kaydet (TextField'da zaten güncellenmiş)
                                    showLocationDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurntOrange
                                )
                            ) {
                                Text("Kaydet", color = SoftCream)
                            }
                        }
                    }
                }
            }
        }

    }
}

// Not Kartı Bileşenleri

// Geliştirilmiş not kartı - görev tamamlama, kategori renkleri ve bildirim göstergeleri ile
@Composable
fun EnhancedNoteCard(note: EnhancedDailyNote, onDelete: () -> Unit, onToggleComplete: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = DeepBrown.copy(alpha = 0.1f)  // Kart arkaplanı için hafif DeepBrown
        ),
        shape = RoundedCornerShape(12.dp),
        // Kategori rengiyle ince bir kenarlık ekle
        border = BorderStroke(2.dp, note.category.color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Üst satır: zaman, kategori göstergesi ve silme butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kategori rengiyle zaman gösterimi
                Text(
                    text = note.hour,
                    fontSize = 14.sp,
                    color = DeepBrown,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(
                            note.category.color.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Row {
                    // Ayarlanmışsa bildirim göstergesi
                    if (note.hasNotification) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Has Notification",
                            tint = BurntOrange,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                    }

                    // Ayarlanmışsa konum göstergesi
                    if (note.hasLocationAlert) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Has Location Alert",
                            tint = BurntOrange,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                    }

                    // Silme butonu
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .background(RustyRed.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RustyRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Görev tamamlama satırı
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Görev tamamlama için onay kutusu
                Checkbox(
                    checked = note.isCompleted,
                    onCheckedChange = onToggleComplete,
                    colors = CheckboxDefaults.colors(
                        checkedColor = note.category.color,
                        uncheckedColor = DeepBrown.copy(alpha = 0.6f)
                    )
                )

                // Tamamlanmışsa üstü çizili başlık
                if (note.isCompleted) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                                append(note.note)
                            }
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBrown.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = note.note,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBrown,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // İçerik (extra_column)
            if (!note.extra_column.isNullOrEmpty()) {
                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    color = DeepBrown.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                // Tamamlanmışsa üstü çizili içerik
                if (note.isCompleted) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                                append(note.extra_column)
                            }
                        },
                        fontSize = 15.sp,
                        color = DeepBrown.copy(alpha = 0.5f),
                        lineHeight = 22.sp
                    )
                } else {
                    Text(
                        text = note.extra_column,
                        fontSize = 15.sp,
                        color = DeepBrown.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                }
            }

            // Ayarlanmışsa bildirim ve konum ayrıntılarını göster
            if (note.notificationTime != null || note.locationName != null) {
                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    color = DeepBrown.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                Column {
                    // Ayarlanmışsa bildirim zamanını göster
                    if (note.notificationTime != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notification Time",
                                tint = BurntOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bildirim: ${note.notificationTime}",
                                fontSize = 14.sp,
                                color = DeepBrown.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Ayarlanmışsa konumu göster
                    if (note.locationName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Location",
                                tint = BurntOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Konum: ${note.locationName}",
                                fontSize = 14.sp,
                                color = DeepBrown.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Kategori etiketi
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
                    .background(
                        note.category.color.copy(alpha = 0.2f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = note.category.label,
                    fontSize = 12.sp,
                    color = note.category.color,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Özel Takvim Görünümü uygulaması
@Composable
fun CustomCalendarView(
    modifier: Modifier = Modifier,
    month: Int,
    year: Int,
    selectedDay: Int,
    onDateSelected: (Int) -> Unit,
    onMonthChanged: (Int, Int) -> Unit
) {
    val daysInMonth = remember(month, year) {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val firstDayOfMonth = remember(month, year) {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        cal.get(Calendar.DAY_OF_WEEK) - 1 // Pazar için 0, Pazartesi için 1 vb.
    }

    val monthNames = remember {
        listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
    }

    val dayNames = remember {
        listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = DeepBrown.copy(alpha = 0.1f)  // Kart arkaplanı için hafif DeepBrown
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Ay/yıl navigasyonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newMonth = if (month == 0) 11 else month - 1
                        val newYear = if (month == 0) year - 1 else year
                        onMonthChanged(newMonth, newYear)
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = BurntOrange
                    )
                }

                Text(
                    text = "${monthNames[month]} $year",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBrown
                )

                IconButton(
                    onClick = {
                        val newMonth = if (month == 11) 0 else month + 1
                        val newYear = if (month == 11) year + 1 else year
                        onMonthChanged(newMonth, newYear)
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Next Month",
                        tint = BurntOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gün adları (Pzt, Sal, vb.)
            Row(modifier = Modifier.fillMaxWidth()) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = BurntOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Takvim ızgarası
            val totalDays = daysInMonth + firstDayOfMonth
            val rows = (totalDays + 6) / 7 // Yukarı yuvarlama

            for (i in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val day = i * 7 + j - firstDayOfMonth + 1

                        if (day in 1..daysInMonth) {
                            // Ayın geçerli günü
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            day == selectedDay -> RustyRed  // Seçili gün için RustyRed
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (day == selectedDay) RustyRed else DeepBrown.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { onDateSelected(day) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 16.sp,
                                    color = if (day == selectedDay) SoftCream else DeepBrown
                                )
                            }
                        } else {
                            // Boş yuva
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
