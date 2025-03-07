package com.example.aboutme

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aboutme.AppColors.BurntOrange
import com.example.aboutme.AppColors.RustyRed
import com.example.aboutme.AppColors.WarmBeige
import com.example.aboutme.ui.theme.SoftCream
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


// Predefined timer categories
val timerCategories = listOf(
    "Çalışma",
    "Meditasyon",
    "Egzersiz",
    "Okuma",
    "Proje",
    "Diğer"
)

@Composable
fun TimerScreen(navController: NavController, sessionManager: SessionManager, timerDao: TimerDao? = null) {
    val context = LocalContext.current
    val timerRecords = remember { mutableStateListOf<TimerRecord>() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val userId = sessionManager.getUserId()
    val username = sessionManager.getUsername() ?: "Kullanıcı"

    // Timer states
    var isRunning by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableStateOf(0L) } // milliseconds
    var timerDescription by remember { mutableStateOf("") }
    var timerCategory by remember { mutableStateOf("Diğer") }
    var isSaving by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Sound effects
    // Note: Sound initialization code removed to focus on UI

    // LaunchedEffect to run the timer
    LaunchedEffect(isRunning) {
        val startTime = System.currentTimeMillis() - timeElapsed
        while (isRunning) {
            timeElapsed = System.currentTimeMillis() - startTime
            delay(10) // 10ms updates
        }
    }

    LaunchedEffect(Unit) {
        if (userId.isNullOrEmpty()) {
            navController.navigate("login_screen") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty() && timerDao != null) {
            timerRecords.clear()
            timerRecords.addAll(timerDao.getTimersForUser(userId))
        }
    }

    // Formatted time string
    val formattedTime by remember {
        derivedStateOf {
            formatTime(timeElapsed)
        }
    }

    fun saveTimer(description: String, category: String, duration: Long) {
        if (!userId.isNullOrEmpty() && timerDao != null) {
            val timerRecord = TimerRecord(
                userId = userId,
                description = description,
                //category = category,
                duration = duration
            )
            coroutineScope.launch {
                timerDao.insertTimer(timerRecord)
                timerRecords.add(0, timerRecord)

                // Show success snackbar
                snackbarMessage = "Zamanlayıcı başarıyla kaydedildi!"
                showSnackbar = true

                // Hide snackbar after 3 seconds
                delay(3000)
                showSnackbar = false
            }
        }
    }

    // Animated progress value (0f-1f)
    val progress by animateFloatAsState(
        targetValue = (timeElapsed % 60000) / 60000f,
        label = "TimerProgress"
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    DeepBrown,                  // Changed to DeepBrown
                    DeepBrown.copy(alpha = 0.8f)  // Subtle gradient
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp) // Padding for bottom navigation
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User greeting and timer title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Merhaba, $username",
                        fontSize = 20.sp,
                        color = SoftCream.copy(alpha = 0.7f),    // Changed to SoftCream
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Zamanlayıcı",
                        fontSize = 30.sp,
                        color = BurntOrange,    // Changed to BurntOrange
                        fontWeight = FontWeight.Bold
                    )
                }

                // Optional: Add a user avatar or icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(BurntOrange.copy(alpha = 0.2f)),    // Changed to BurntOrange
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "User Timer",
                        tint = BurntOrange,    // Changed to BurntOrange
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer display
            Card(
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Background and progress circle
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background circle
                        drawArc(
                            color = SoftCream.copy(alpha = 0.1f),    // Changed to SoftCream
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round),
                            size = Size(size.width, size.height),
                            topLeft = Offset(0f, 0f)
                        )

                        // Progress circle
                        drawArc(
                            color = RustyRed,    // Changed to RustyRed
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round),
                            size = Size(size.width, size.height),
                            topLeft = Offset(0f, 0f)
                        )
                    }

                    // Time display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formattedTime,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftCream    // Changed to SoftCream
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Reset button
                IconButton(
                    onClick = {
                        isRunning = false
                        timeElapsed = 0L
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(DeepBrown.copy(alpha = 0.7f))    // Changed to DeepBrown
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = SoftCream,    // Changed to SoftCream
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Start/Stop button
                IconButton(
                    onClick = {
                        isRunning = !isRunning
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning) RustyRed.copy(alpha = 0.8f) else BurntOrange    // Changed colors
                        )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = SoftCream,    // Changed to SoftCream
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Save button (inactive if no time)
                IconButton(
                    onClick = {
                        isSaving = !isSaving
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (timeElapsed > 0) WarmBeige else DeepBrown.copy(alpha = 0.5f)    // Changed colors
                        ),
                    enabled = timeElapsed > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = SoftCream,    // Changed to SoftCream
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save form (animated visibility)
            AnimatedVisibility(
                visible = isSaving,
                enter = fadeIn() + expandVertically(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut() + shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WarmBeige.copy(alpha = 0.2f)    // Changed to semi-transparent WarmBeige
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Zamanlayıcıyı Kaydet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = BurntOrange    // Changed to BurntOrange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description TextField
                        OutlinedTextField(
                            value = timerDescription,
                            onValueChange = { timerDescription = it },
                            label = {
                                Text(
                                    "Açıklama",
                                    color = SoftCream.copy(alpha = 0.8f)    // Changed to SoftCream
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = "Description",
                                    tint = BurntOrange    // Changed to BurntOrange
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BurntOrange,    // Changed to BurntOrange
                                unfocusedBorderColor = SoftCream.copy(alpha = 0.5f),    // Changed to SoftCream
                                focusedTextColor = SoftCream,    // Changed to SoftCream
                                unfocusedTextColor = SoftCream,    // Changed to SoftCream
                                cursorColor = BurntOrange    // Changed to BurntOrange
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Dropdown
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryDropdown = true }
                                .background(
                                    color = DeepBrown.copy(alpha = 0.3f),    // Changed to semi-transparent DeepBrown
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kategori: $timerCategory",
                                color = SoftCream,    // Changed to SoftCream
                                fontSize = 16.sp
                            )

                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Select Category",
                                tint = BurntOrange    // Changed to BurntOrange
                            )

                            // Dropdown Menu
                            DropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false },
                                modifier = Modifier
                                    .background(DeepBrown.copy(alpha = 0.9f))    // Changed to semi-transparent DeepBrown
                                    .width(200.dp)
                            ) {
                                timerCategories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = category,
                                                color = if (category == timerCategory) BurntOrange else SoftCream    // Changed colors
                                            )
                                        },
                                        onClick = {
                                            timerCategory = category
                                            showCategoryDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    isSaving = false
                                    timerDescription = ""
                                    timerCategory = "Diğer"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepBrown.copy(alpha = 0.7f)    // Changed to semi-transparent DeepBrown
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("İptal", color = SoftCream)    // Changed to SoftCream
                            }

                            Button(
                                onClick = {
                                    if (timerDescription.isNotEmpty()) {
                                        // Stop the timer
                                        isRunning = false

                                        // Save the timer record
                                        saveTimer(
                                            description = timerDescription,
                                            category = timerCategory,
                                            duration = timeElapsed
                                        )

                                        // Reset timer states
                                        timeElapsed = 0L
                                        timerDescription = ""
                                        timerCategory = "Diğer"
                                        isSaving = false
                                    } else {
                                        // Show error or highlight description field
                                        snackbarMessage = "Lütfen bir açıklama girin"
                                        showSnackbar = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurntOrange    // Changed to BurntOrange
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Kaydet", color = SoftCream)    // Changed to SoftCream
                            }
                        }
                    }
                }
            }

            // Snackbar for notifications
            AnimatedVisibility(
                visible = showSnackbar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    containerColor = RustyRed,    // Changed to RustyRed
                    contentColor = SoftCream    // Changed to SoftCream
                ) {
                    Text(text = snackbarMessage)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Saved timers list
            if (timerRecords.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DeepBrown.copy(alpha = 0.3f)    // Changed to semi-transparent DeepBrown
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kaydedilmiş Zamanlayıcılar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = BurntOrange    // Changed to BurntOrange
                            )

                            // Optional: Add a filter or sort button
                            IconButton(onClick = { /* Implement sorting/filtering */ }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Filter",
                                    tint = SoftCream    // Changed to SoftCream
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Saved timers
                        Column {
                            timerRecords.forEachIndexed { index, record ->
                                TimerRecordItem(
                                    record = record,
                                    onDelete = {
                                        timerRecords.remove(record)
                                        // If DAO exists, delete from database
                                        if (timerDao != null) {
                                            coroutineScope.launch {
                                                timerDao.deleteTimer(record)

                                                // Show deletion notification
                                                snackbarMessage = "Zamanlayıcı silindi"
                                                showSnackbar = true
                                                delay(3000)
                                                showSnackbar = false
                                            }
                                        }
                                    }
                                )

                                if (index < timerRecords.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = SoftCream.copy(alpha = 0.2f)    // Changed to SoftCream
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom padding
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Note: Bottom Navigation is now handled in MainApp
    }
}

// Enhanced TimerRecordItem to show category
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
            // Category chip
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(
                        color = WarmBeige.copy(alpha = 0.2f),    // Changed to semi-transparent WarmBeige
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = record.category ?: "Diğer",
                    color = WarmBeige,    // Changed to WarmBeige
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = record.description,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = SoftCream    // Changed to SoftCream
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Süre: ${formatTime(record.duration)}",
                color = BurntOrange,    // Changed to BurntOrange
                fontSize = 16.sp
            )
            Text(
                text = formattedDate,
                color = SoftCream.copy(alpha = 0.6f),    // Changed to SoftCream
                fontSize = 14.sp
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .clip(CircleShape)
                .background(RustyRed.copy(alpha = 0.1f))    // Changed to semi-transparent RustyRed
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = RustyRed    // Changed to RustyRed
            )
        }
    }
}

// Utility function to format time
@SuppressLint("DefaultLocale")
fun formatTime(timeInMillis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    val millis = (timeInMillis % 1000) / 10 // Hundredths

    return String.format(
        "%02d:%02d:%02d.%02d",
        hours, minutes, seconds, millis
    )
}