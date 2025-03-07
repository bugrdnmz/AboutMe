package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Geliştirilmiş alışkanlık takip bileşeni
@Composable
fun SimpleHabitTracker(
    sessionManager: SessionManager,
    habitTrackerDao: HabitTrackerDao? = null
) {
    val userId = sessionManager.getUserId() ?: return
    val coroutineScope = rememberCoroutineScope()

    // State'ler
    var habits by remember { mutableStateOf<List<HabitTracker>>(emptyList()) }
    var selectedHabit by remember { mutableStateOf<HabitTracker?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<HabitTracker?>(null) }


    // Kullanıcının alışkanlıklarını yükle
    LaunchedEffect(userId) {
        habitTrackerDao?.let { dao ->
            try {
                println("HabitTracker: Veritabanından alışkanlıklar yükleniyor...")

                habits = dao.getHabitsForUser(userId)
                println("HabitTracker: ${habits.size} alışkanlık yüklendi")

                if (habits.isNotEmpty() && selectedHabit == null) {
                    selectedHabit = habits[0]
                }

                // Alışkanlıkların seri kontrolünü yap
                val updatedHabits = checkAndUpdateStreaks(habits)
                if (updatedHabits.any { it.currentStreak != habits.find { h -> h.id == it.id }?.currentStreak }) {
                    habits = updatedHabits
                    updatedHabits.forEach { updatedHabit ->
                        if (updatedHabit.currentStreak == 0) {
                            coroutineScope.launch {
                                println("HabitTracker: Sıfırlanan seri veritabanına kaydediliyor...")
                                dao.updateHabit(updatedHabit)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("HabitTracker Hata: ${e.message}")
                e.printStackTrace()
                habits = emptyList()
            }
        } ?: run {
            println("HabitTracker: habitTrackerDao null!")
        }
    }
    fun deleteHabit(habit: HabitTracker) {
        coroutineScope.launch {
            try {
                if (habitTrackerDao != null) {
                    // Delete from database
                    habitTrackerDao.deleteHabit(habit)

                    // Refresh habits list
                    val updatedHabits = habitTrackerDao.getHabitsForUser(userId)
                    habits = updatedHabits

                    // Reset selected habit if necessary
                    selectedHabit = if (updatedHabits.isNotEmpty()) updatedHabits[0] else null

                    // Show success message
                    message = "Alışkanlık silindi: ${habit.habitName}"
                    showMessage = true
                }
            } catch (e: Exception) {
                println("HabitTracker Hata: Silme sırasında hata - ${e.message}")
                e.printStackTrace()
                message = "Alışkanlık silinirken hata oluştu!"
                showMessage = true
            }
        }
    }

    // Mesaj gösterme
    if (showMessage) {
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            showMessage = false
        }

        Snackbar(
            modifier = Modifier.padding(8.dp),
            containerColor = AppColors.Terracotta,
            contentColor = Color.White
        ) {
            Text(message)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightSand)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "40 Günlük Alışkanlık Takibi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DarkBrown
                )

                // Ekle butonu
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.Bronze)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Alışkanlık Ekle",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alışkanlık listesi
            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz bir alışkanlık eklemediniz.",
                        color = AppColors.TextLight
                    )
                }
            } else {
                // Alışkanlıkları listele
                Column {
                    habits.forEach { habit ->
                        SimpleHabitItem(
                            habit = habit,
                            isSelected = selectedHabit?.id == habit.id,
                            onClick = { selectedHabit = habit },
                            onCheckIn = {
                                // Seri kontrolünü yap, bugün işaretlenmiş mi kontrol et
                                if (isCheckedToday(habit.lastCheckDate)) {
                                    message = "Bu alışkanlığı bugün zaten işaretlediniz!"
                                    showMessage = true
                                    return@SimpleHabitItem
                                }

                                // Günlük işaretleme
                                coroutineScope.launch {
                                    val currentTime = System.currentTimeMillis()
                                    val updatedHabit = habit.copy(
                                        lastCheckDate = currentTime,
                                        currentStreak = habit.currentStreak + 1,
                                        bestStreak = maxOf(habit.bestStreak, habit.currentStreak + 1)
                                    )

                                    try {
                                        println("HabitTracker: Alışkanlık işaretleniyor: ${updatedHabit.habitName}")

                                        // Veritabanına kaydet
                                        if (habitTrackerDao != null) {
                                            habitTrackerDao.updateHabit(updatedHabit)
                                            println("HabitTracker: Alışkanlık güncellendi, yeni seri: ${updatedHabit.currentStreak}")

                                            // İsteğe bağlı: Veritabanından tekrar al ve doğrula
                                            val refreshedHabit = habitTrackerDao.getHabitsForUser(userId)
                                                .find { it.id == updatedHabit.id }
                                            println("HabitTracker: Veritabanından alınan güncel seri: ${refreshedHabit?.currentStreak}")
                                        } else {
                                            println("HabitTracker: habitTrackerDao null!")
                                        }

                                        // Listeyi güncelle
                                        habits = habits.map {
                                            if (it.id == updatedHabit.id) updatedHabit else it
                                        }

                                        // Seçili alışkanlığı güncelle
                                        if (selectedHabit?.id == updatedHabit.id) {
                                            selectedHabit = updatedHabit
                                        }

                                        // Başarı mesajı göster
                                        message = if (updatedHabit.currentStreak >= 40) {
                                            "Tebrikler! 40 günlük hedefinize ulaştınız! 🎉"
                                        } else {
                                            "Günlük ilerlemeniz kaydedildi! Seri: ${updatedHabit.currentStreak}/40"
                                        }
                                        showMessage = true
                                    } catch (e: Exception) {
                                        println("HabitTracker Hata: İşaretleme sırasında hata - ${e.message}")
                                        e.printStackTrace()
                                        message = "İşaretleme sırasında hata oluştu!"
                                        showMessage = true
                                    }
                                }
                            },
                            onDelete = {
                                habitToDelete = habit
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                }

                if (showDeleteConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteConfirmDialog = false
                            habitToDelete = null
                        },
                        title = { Text("Alışkanlığı Sil") },
                        text = {
                            Text(
                                "\"${habitToDelete?.habitName}\" alışkanlığını silmek istediğinizden emin misiniz? " +
                                        "Bu işlem geri alınamaz."
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    habitToDelete?.let { deleteHabit(it) }
                                    showDeleteConfirmDialog = false
                                    habitToDelete = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Rust)
                            ) {
                                Text("Sil")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteConfirmDialog = false
                                    habitToDelete = null
                                }
                            ) {
                                Text("İptal")
                            }
                        }
                    )
                }

                // Seçili alışkanlık detayları
                selectedHabit?.let { habit ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = AppColors.Sand)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Alışkanlık detayları
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = habit.habitName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.DarkBrown
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // İlerleme çubuğu
                        LinearProgressIndicator(
                            progress = { habit.currentStreak.toFloat() / 40f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = AppColors.Terracotta,
                            trackColor = AppColors.Sand
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (habit.bestStreak > 0) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "En iyi seri",
                                    tint = AppColors.Bronze,
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    text = " En iyi: ${habit.bestStreak} gün",
                                    fontSize = 14.sp,
                                    color = AppColors.TextDark
                                )
                            }
                        }

                        // Son işaretleme bilgisi
                        habit.lastCheckDate?.let {
                            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr"))
                            Text(
                                text = "Son işaretlenme: ${dateFormat.format(Date(it))}",
                                fontSize = 14.sp,
                                color = AppColors.TextLight,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Alışkanlık ekleme dialogu
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Yeni Alışkanlık Ekle") },
            text = {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text("Alışkanlık Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHabitName.isNotBlank()) {
                            val newHabit = HabitTracker(
                                userId = userId,
                                habitName = newHabitName,
                                checkDates = emptyList() // Boş liste olarak başlat
                            )
                            coroutineScope.launch {
                                try {
                                    println("HabitTracker: Yeni alışkanlık kaydediliyor: ${newHabit.habitName}")

                                    // Veritabanına kaydet
                                    if (habitTrackerDao != null) {
                                        habitTrackerDao.insertHabit(newHabit)
                                        println("HabitTracker: Alışkanlık veritabanına kaydedildi")

                                        // Veritabanından tekrar alışkanlıkları yükle (doğrulama amaçlı)
                                        val updatedHabits = habitTrackerDao.getHabitsForUser(userId)
                                        println("HabitTracker: Güncel alışkanlık sayısı: ${updatedHabits.size}")

                                        // State'i güncelle
                                        habits = updatedHabits
                                        selectedHabit = updatedHabits.find { it.habitName == newHabitName }
                                    } else {
                                        // DAO null ise sadece state'i güncelle (test amaçlı)
                                        println("HabitTracker: habitTrackerDao null olduğu için sadece state güncellendi")
                                        habits = habits + newHabit
                                        selectedHabit = newHabit
                                    }

                                    message = "Alışkanlık başarıyla eklendi!"
                                    showMessage = true
                                } catch (e: Exception) {
                                    println("HabitTracker Hata: ${e.message}")
                                    e.printStackTrace()
                                    message = "Alışkanlık eklenirken hata oluştu: ${e.message}"
                                    showMessage = true
                                }
                                newHabitName = ""
                                showAddDialog = false
                            }
                        }
                    }
                ) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun SimpleHabitItem(
    habit: HabitTracker,
    isSelected: Boolean,
    onClick: () -> Unit,
    onCheckIn: () -> Unit,
    onDelete: () -> Unit  // Yeni parametre
) {
    var showDeleteOption by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                AppColors.Terracotta.copy(alpha = 0.1f)
            else
                AppColors.LightSand
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alışkanlık bilgileri
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = habit.habitName,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                        color = AppColors.DarkBrown
                    )

                    // Silme ikonu
                    IconButton(
                        onClick = { showDeleteOption = !showDeleteOption },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete, // Delete ikonunu ekle
                            contentDescription = "Alışkanlığı Sil",
                            tint = AppColors.Rust
                        )
                    }
                }

                Text(
                    text = "Seri: ${habit.currentStreak}/40 gün",
                    fontSize = 14.sp,
                    color = AppColors.TextLight
                )
            }

            // İşaretle butonu
            val canCheckToday = !isCheckedToday(habit.lastCheckDate)
            Button(
                onClick = {
                    if (showDeleteOption) {
                        onDelete()
                    } else {
                        onCheckIn()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showDeleteOption)
                        AppColors.Rust
                    else if (canCheckToday)
                        AppColors.Bronze
                    else
                        AppColors.TextLight,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                enabled = canCheckToday || showDeleteOption
            ) {
                Icon(
                    imageVector = if (showDeleteOption) Icons.Default.Delete else Icons.Default.Check,
                    contentDescription = if (showDeleteOption) "Sil" else "İşaretle",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showDeleteOption) "Sil" else
                        if (canCheckToday) "İşaretle" else "Tamamlandı",
                    fontSize = 12.sp
                )
            }
        }
    }
}
// Bugün işaretlenmiş mi kontrol et
fun isCheckedToday(lastCheckDate: Long?): Boolean {
    if (lastCheckDate == null) return false

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val lastCheck = Calendar.getInstance().apply {
        timeInMillis = lastCheckDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return today.timeInMillis == lastCheck.timeInMillis
}

// Bir günden fazla geçmişse seriyi sıfırla
fun checkAndUpdateStreaks(habits: List<HabitTracker>): List<HabitTracker> {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return habits.map { habit ->
        if (habit.lastCheckDate == null) return@map habit

        val lastCheck = Calendar.getInstance().apply {
            timeInMillis = habit.lastCheckDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Dün veya bugün işaretlenmemişse sıfırla
        if (lastCheck.timeInMillis < yesterday.timeInMillis) {
            habit.copy(currentStreak = 0)
        } else {
            habit
        }
    }
}