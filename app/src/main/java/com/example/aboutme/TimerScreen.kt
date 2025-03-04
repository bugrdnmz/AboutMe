package com.example.aboutme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Timer kaydı için veri sınıfı
data class TimerRecord(
    val id: Int = 0,
    val description: String,
    val duration: Long, // Milisaniye
    val date: Long = System.currentTimeMillis() // Kayıt tarihi
)

// TimerDao arayüzü (Room için)
interface TimerDao {
    suspend fun insertTimer(timer: TimerRecord): Long
    suspend fun deleteTimer(timer: TimerRecord)
    suspend fun getAllTimers(): List<TimerRecord>
}

@Composable
fun TimerScreen(navController: NavController) {
    // Gerçek DAO eklenmediği için sahte bir liste kullanıyoruz
    val timerRecords = remember { mutableStateListOf<TimerRecord>() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Timer durumları
    var isRunning by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableStateOf(0L) } // milisaniye
    var timerDescription by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Timer'ı çalıştırmak için LaunchedEffect
    LaunchedEffect(isRunning) {
        val startTime = System.currentTimeMillis() - timeElapsed
        while (isRunning) {
            timeElapsed = System.currentTimeMillis() - startTime
            delay(10) // 10ms güncellemeler
        }
    }

    // Formatlanmış zaman stringleri
    val formattedTime by remember {
        derivedStateOf {
            formatTime(timeElapsed)
        }
    }

    // Animasyonlu ilerleme çubuğu için değer (0f-1f arası)
    val progress by animateFloatAsState(
        targetValue = (timeElapsed % 60000) / 60000f,
        label = "TimerProgress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp) // Bottom navigation için padding
                .verticalScroll(scrollState), // Sayfayı kaydırılabilir yap
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Zamanlayıcı",
                fontSize = 30.sp,
                color = Color(0xFFFB8C00),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Kronometre gösterimi
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // İlerleme çemberi
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Arka plan çember
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12f, cap = StrokeCap.Round),
                        size = Size(size.width, size.height),
                        topLeft = Offset(0f, 0f)
                    )

                    // İlerleme çemberi
                    drawArc(
                        color = Color(0xFFFB8C00),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 12f, cap = StrokeCap.Round),
                        size = Size(size.width, size.height),
                        topLeft = Offset(0f, 0f)
                    )
                }

                // Zaman gösterimi
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer kontrol butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Reset butonu
                IconButton(
                    onClick = {
                        isRunning = false
                        timeElapsed = 0L
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.LightGray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Başlat/Durdur butonu
                IconButton(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            if (isRunning) Color.Red else Color(0xFF4CAF50),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Kaydet butonu (aktif değilse gri)
                IconButton(
                    onClick = { isSaving = !isSaving },
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            if (timeElapsed > 0) Color(0xFF2196F3) else Color.Gray,
                            CircleShape
                        ),
                    enabled = timeElapsed > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kaydetme formu (görünürlüğü isSaving ile kontrol edilir)
            if (isSaving) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Zamanlayıcıyı Kaydet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = timerDescription,
                            onValueChange = { timerDescription = it },
                            label = { Text("Açıklama") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { isSaving = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                )
                            ) {
                                Text("İptal")
                            }

                            Button(
                                onClick = {
                                    if (timerDescription.isNotEmpty()) {
                                        // Yeni kayıt ekle
                                        val newRecord = TimerRecord(
                                            id = timerRecords.size + 1,
                                            description = timerDescription,
                                            duration = timeElapsed
                                        )
                                        timerRecords.add(0, newRecord) // En başa ekle
                                        timerDescription = ""
                                        isSaving = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFB8C00)
                                )
                            ) {
                                Text("Kaydet")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kaydedilmiş zamanlayıcılar listesi
            if (timerRecords.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kaydedilmiş Zamanlayıcılar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = Color(0xFFFB8C00)
                        )

                        // Kaydedilmiş zamanlayıcılar
                        Column {
                            timerRecords.forEachIndexed { index, record ->
                                TimerRecordItem(
                                    record = record,
                                    onDelete = {
                                        timerRecords.remove(record)
                                    }
                                )

                                if (index < timerRecords.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom padding to ensure the content doesn't hide behind the nav bar
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Navigation Bar
        AppBottomNavigation(
            navController = navController,
            onAddClick = { isRunning = !isRunning },
            //fabIcon = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun TimerRecordItem(record: TimerRecord, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr", "TR")) }
    val formattedDate = remember(record.date) { dateFormat.format(Date(record.date)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.description,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Süre: ${formatTime(record.duration)}",
                color = Color(0xFFFB8C00),
                fontSize = 14.sp
            )
            Text(
                text = formattedDate,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}

// Milisaniyeleri formatla: "00:00:00" (saat:dakika:saniye)
fun formatTime(timeInMillis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    val millis = (timeInMillis % 1000) / 10 // Yüzde birlik kısım

    return String.format(
        "%02d:%02d:%02d.%02d",
        hours, minutes, seconds, millis
    )
}