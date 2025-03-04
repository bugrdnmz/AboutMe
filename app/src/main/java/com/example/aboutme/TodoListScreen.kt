package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun TodoListScreen(taskDao: TaskDao, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val tasks = remember { mutableStateListOf<Task>() }

    // Common state variables
    var newTaskName by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Gray) }

    // Title styling state variables
    var selectedTitleTextColor by remember { mutableStateOf(Color.White) }
    var selectedTitleFontFamily by remember { mutableStateOf("Default") }
    var selectedTitleFontSize by remember { mutableStateOf(18) }
    var selectedTitleFontWeight by remember { mutableStateOf("Normal") }

    // Description styling state variables
    var selectedDescTextColor by remember { mutableStateOf(Color.White) }
    var selectedDescFontFamily by remember { mutableStateOf("Default") }
    var selectedDescFontSize by remember { mutableStateOf(14) }
    var selectedDescFontWeight by remember { mutableStateOf("Normal") }

    var isEditingTask by remember { mutableStateOf<Task?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(Unit) {
        tasks.addAll(taskDao.getAllTasks())
    }

    fun resetForm() {
        newTaskName = ""
        newTaskDescription = ""
        selectedColor = Color.Gray

        // Reset title styling
        selectedTitleTextColor = Color.White
        selectedTitleFontFamily = "Default"
        selectedTitleFontSize = 18
        selectedTitleFontWeight = "Normal"

        // Reset description styling
        selectedDescTextColor = Color.White
        selectedDescFontFamily = "Default"
        selectedDescFontSize = 14
        selectedDescFontWeight = "Normal"

        isEditingTask = null
        isAddingTask = false
    }

    fun addTask() {
        if (newTaskName.isNotEmpty()) {
            val task = Task(
                taskName = newTaskName,
                description = newTaskDescription,
                color = selectedColor.toArgb(),

                // Title styling properties
                titleTextColor = selectedTitleTextColor.toArgb(),
                titleFontFamily = selectedTitleFontFamily,
                titleFontSize = selectedTitleFontSize,
                titleFontWeight = selectedTitleFontWeight,

                // Description styling properties
                descTextColor = selectedDescTextColor.toArgb(),
                descFontFamily = selectedDescFontFamily,
                descFontSize = selectedDescFontSize,
                descFontWeight = selectedDescFontWeight,

                // For backward compatibility
                textColor = selectedTitleTextColor.toArgb(),
                fontFamily = selectedTitleFontFamily,
                fontSize = selectedTitleFontSize,
                fontWeight = selectedTitleFontWeight
            )
            coroutineScope.launch {
                taskDao.insertTask(task)
                tasks.add(task)
                resetForm()
            }
        }
    }

    fun deleteTask(task: Task) {
        taskToDelete = task
    }

    @Composable
    fun TaskCard(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
        // Title styling
        val titleFontFamily = when (task.titleFontFamily.ifEmpty { task.fontFamily }) {
            "Serif" -> FontFamily.Serif
            "Monospace" -> FontFamily.Monospace
            "Cursive" -> FontFamily.Cursive
            "SansSerif" -> FontFamily.SansSerif
            else -> FontFamily.Default
        }

        val titleFontWeight = when (task.titleFontWeight.ifEmpty { task.fontWeight }) {
            "Bold" -> FontWeight.Bold
            "Light" -> FontWeight.Light
            "Medium" -> FontWeight.Medium
            else -> FontWeight.Normal
        }

        // Description styling
        val descFontFamily = when (task.descFontFamily.ifEmpty { task.fontFamily }) {
            "Serif" -> FontFamily.Serif
            "Monospace" -> FontFamily.Monospace
            "Cursive" -> FontFamily.Cursive
            "SansSerif" -> FontFamily.SansSerif
            else -> FontFamily.Default
        }

        val descFontWeight = when (task.descFontWeight.ifEmpty { task.fontWeight }) {
            "Bold" -> FontWeight.Bold
            "Light" -> FontWeight.Light
            "Medium" -> FontWeight.Medium
            else -> FontWeight.Normal
        }

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable(onClick = onEdit),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(task.color))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Text(
                    text = task.taskName,
                    fontSize = (task.titleFontSize.takeIf { it > 0 } ?: task.fontSize).sp,
                    color = Color(task.titleTextColor.takeIf { it != 0 } ?: task.textColor),
                    fontFamily = titleFontFamily,
                    fontWeight = titleFontWeight,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = task.description,
                    fontSize = (task.descFontSize.takeIf { it > 0 } ?: task.fontSize - 4).sp,
                    color = Color(task.descTextColor.takeIf { it != 0 } ?: task.textColor).copy(alpha = 0.7f),
                    fontFamily = descFontFamily,
                    fontWeight = descFontWeight,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Delete button
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .clickable { onDelete() }
                            .padding(4.dp)
                            .size(24.dp),
                        tint = Color(task.titleTextColor.takeIf { it != 0 } ?: task.textColor)
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp) // Add padding for bottom navigation
        ) {
            Text(
                text = "Notes",
                fontSize = 30.sp,
                color = Color(0xFFFB8C00),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    TaskCard(task, { isEditingTask = task }, { deleteTask(task) })
                }
            }
        }

        // Bottom Navigation with centered FAB
        AppBottomNavigation(
            navController = navController,
            onAddClick = { isAddingTask = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }

    // Delete Confirmation Dialog
    if (taskToDelete != null) {
        Dialog(onDismissRequest = { taskToDelete = null }) {
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Silmek istediğinize emin misiniz?", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = {
                            taskToDelete?.let {
                                coroutineScope.launch {
                                    taskDao.deleteTask(it)
                                    tasks.remove(it)
                                }
                                taskToDelete = null
                            }
                        }) { Text("Evet") }
                        Button(onClick = { taskToDelete = null }) { Text("Hayır") }
                    }
                }
            }
        }
    }

    // Task Editing Dialog
    if (isEditingTask != null) {
        var editTaskName by remember(isEditingTask) { mutableStateOf(isEditingTask!!.taskName) }
        var editTaskDescription by remember(isEditingTask) { mutableStateOf(isEditingTask!!.description) }
        var editTaskColor by remember(isEditingTask) { mutableStateOf(Color(isEditingTask!!.color)) }

        // Title styling
        var editTitleTextColor by remember(isEditingTask) {
            mutableStateOf(Color(isEditingTask!!.titleTextColor.takeIf { it != 0 } ?: isEditingTask!!.textColor))
        }
        var editTitleFontFamily by remember(isEditingTask) {
            mutableStateOf(isEditingTask!!.titleFontFamily.ifEmpty { isEditingTask!!.fontFamily })
        }
        var editTitleFontSize by remember(isEditingTask) {
            mutableStateOf(isEditingTask!!.titleFontSize.takeIf { it > 0 } ?: isEditingTask!!.fontSize)
        }
        var editTitleFontWeight by remember(isEditingTask) {
            mutableStateOf(isEditingTask!!.titleFontWeight.ifEmpty { isEditingTask!!.fontWeight })
        }

        // Description styling
        var editDescTextColor by remember(isEditingTask) {
            mutableStateOf(Color(isEditingTask!!.descTextColor.takeIf { it != 0 } ?: isEditingTask!!.textColor))
        }
        var editDescFontFamily by remember(isEditingTask) {
            mutableStateOf(isEditingTask!!.descFontFamily.ifEmpty { isEditingTask!!.fontFamily })
        }
        var editDescFontSize by remember(isEditingTask) {
            mutableStateOf(isEditingTask!!.descFontSize.takeIf { it > 0 } ?: (isEditingTask!!.fontSize - 4))
        }
        var editDescFontWeight by remember(isEditingTask) {
            mutableStateOf(isEditingTask!!.descFontWeight.ifEmpty { isEditingTask!!.fontWeight })
        }

        var selectedTabIndex by remember { mutableIntStateOf(0) }

        Dialog(onDismissRequest = { isEditingTask = null }) {
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = editTaskName,
                        onValueChange = { editTaskName = it },
                        label = { Text("Görev Adı") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = editTaskDescription,
                        onValueChange = { editTaskDescription = it },
                        label = { Text("Açıklama") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tab Row for different styling options
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Arka Plan") }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Başlık") }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Açıklama") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (selectedTabIndex) {
                        0 -> ColorSelector(selectedColor = editTaskColor, onColorChange = { editTaskColor = it })
                        1 -> TextStyleSelector(
                            selectedTextColor = editTitleTextColor,
                            selectedFontFamily = editTitleFontFamily,
                            selectedFontSize = editTitleFontSize,
                            selectedFontWeight = editTitleFontWeight,
                            onTextColorChange = { editTitleTextColor = it },
                            onFontFamilyChange = { editTitleFontFamily = it },
                            onFontSizeChange = { editTitleFontSize = it },
                            onFontWeightChange = { editTitleFontWeight = it },
                            title = "Başlık Stili"
                        )
                        2 -> TextStyleSelector(
                            selectedTextColor = editDescTextColor,
                            selectedFontFamily = editDescFontFamily,
                            selectedFontSize = editDescFontSize,
                            selectedFontWeight = editDescFontWeight,
                            onTextColorChange = { editDescTextColor = it },
                            onFontFamilyChange = { editDescFontFamily = it },
                            onFontSizeChange = { editDescFontSize = it },
                            onFontWeightChange = { editDescFontWeight = it },
                            title = "Açıklama Stili"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preview of styling
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = editTaskColor)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Önizleme: " + editTaskName,
                                color = editTitleTextColor,
                                fontSize = editTitleFontSize.sp,
                                fontFamily = when (editTitleFontFamily) {
                                    "Serif" -> FontFamily.Serif
                                    "Monospace" -> FontFamily.Monospace
                                    "Cursive" -> FontFamily.Cursive
                                    "SansSerif" -> FontFamily.SansSerif
                                    else -> FontFamily.Default
                                },
                                fontWeight = when (editTitleFontWeight) {
                                    "Bold" -> FontWeight.Bold
                                    "Light" -> FontWeight.Light
                                    "Medium" -> FontWeight.Medium
                                    else -> FontWeight.Normal
                                }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = editTaskDescription,
                                color = editDescTextColor,
                                fontSize = editDescFontSize.sp,
                                fontFamily = when (editDescFontFamily) {
                                    "Serif" -> FontFamily.Serif
                                    "Monospace" -> FontFamily.Monospace
                                    "Cursive" -> FontFamily.Cursive
                                    "SansSerif" -> FontFamily.SansSerif
                                    else -> FontFamily.Default
                                },
                                fontWeight = when (editDescFontWeight) {
                                    "Bold" -> FontWeight.Bold
                                    "Light" -> FontWeight.Light
                                    "Medium" -> FontWeight.Medium
                                    else -> FontWeight.Normal
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = {
                            val updatedTask = isEditingTask!!.copy(
                                taskName = editTaskName,
                                description = editTaskDescription,
                                color = editTaskColor.toArgb(),

                                // Title styling
                                titleTextColor = editTitleTextColor.toArgb(),
                                titleFontFamily = editTitleFontFamily,
                                titleFontSize = editTitleFontSize,
                                titleFontWeight = editTitleFontWeight,

                                // Description styling
                                descTextColor = editDescTextColor.toArgb(),
                                descFontFamily = editDescFontFamily,
                                descFontSize = editDescFontSize,
                                descFontWeight = editDescFontWeight,

                                // For backward compatibility
                                textColor = editTitleTextColor.toArgb(),
                                fontFamily = editTitleFontFamily,
                                fontSize = editTitleFontSize,
                                fontWeight = editTitleFontWeight
                            )
                            coroutineScope.launch {
                                taskDao.updateTask(updatedTask)
                                val index = tasks.indexOfFirst { it.id == updatedTask.id }
                                if (index != -1) {
                                    tasks[index] = updatedTask
                                }
                                isEditingTask = null
                            }
                        }) { Text("Güncelle") }
                        Button(onClick = { isEditingTask = null }) { Text("İptal") }
                    }
                }
            }
        }
    }

    // Task Adding Dialog
    if (isAddingTask) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        Dialog(onDismissRequest = { isAddingTask = false }) {
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = newTaskName,
                        onValueChange = { newTaskName = it },
                        label = { Text("Görev Adı") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newTaskDescription,
                        onValueChange = { newTaskDescription = it },
                        label = { Text("Açıklama") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tab Row for different styling options
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Arka Plan") }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Başlık") }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("Açıklama") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (selectedTabIndex) {
                        0 -> ColorSelector(selectedColor = selectedColor, onColorChange = { selectedColor = it })
                        1 -> TextStyleSelector(
                            selectedTextColor = selectedTitleTextColor,
                            selectedFontFamily = selectedTitleFontFamily,
                            selectedFontSize = selectedTitleFontSize,
                            selectedFontWeight = selectedTitleFontWeight,
                            onTextColorChange = { selectedTitleTextColor = it },
                            onFontFamilyChange = { selectedTitleFontFamily = it },
                            onFontSizeChange = { selectedTitleFontSize = it },
                            onFontWeightChange = { selectedTitleFontWeight = it },
                            title = "Başlık Stili"
                        )
                        2 -> TextStyleSelector(
                            selectedTextColor = selectedDescTextColor,
                            selectedFontFamily = selectedDescFontFamily,
                            selectedFontSize = selectedDescFontSize,
                            selectedFontWeight = selectedDescFontWeight,
                            onTextColorChange = { selectedDescTextColor = it },
                            onFontFamilyChange = { selectedDescFontFamily = it },
                            onFontSizeChange = { selectedDescFontSize = it },
                            onFontWeightChange = { selectedDescFontWeight = it },
                            title = "Açıklama Stili"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preview of styling
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = selectedColor)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Önizleme: " + newTaskName,
                                color = selectedTitleTextColor,
                                fontSize = selectedTitleFontSize.sp,
                                fontFamily = when (selectedTitleFontFamily) {
                                    "Serif" -> FontFamily.Serif
                                    "Monospace" -> FontFamily.Monospace
                                    "Cursive" -> FontFamily.Cursive
                                    "SansSerif" -> FontFamily.SansSerif
                                    else -> FontFamily.Default
                                },
                                fontWeight = when (selectedTitleFontWeight) {
                                    "Bold" -> FontWeight.Bold
                                    "Light" -> FontWeight.Light
                                    "Medium" -> FontWeight.Medium
                                    else -> FontWeight.Normal
                                }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = newTaskDescription,
                                color = selectedDescTextColor,
                                fontSize = selectedDescFontSize.sp,
                                fontFamily = when (selectedDescFontFamily) {
                                    "Serif" -> FontFamily.Serif
                                    "Monospace" -> FontFamily.Monospace
                                    "Cursive" -> FontFamily.Cursive
                                    "SansSerif" -> FontFamily.SansSerif
                                    else -> FontFamily.Default
                                },
                                fontWeight = when (selectedDescFontWeight) {
                                    "Bold" -> FontWeight.Bold
                                    "Light" -> FontWeight.Light
                                    "Medium" -> FontWeight.Medium
                                    else -> FontWeight.Normal
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { addTask() }) { Text("Ekle") }
                        Button(onClick = { isAddingTask = false }) { Text("İptal") }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelector(selectedColor: Color, onColorChange: (Color) -> Unit) {
    Column {
        Text("Arka Plan Rengi")
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            val colors = listOf(
                Color.Gray, Color.Red, Color.Green, Color.Blue, Color.Yellow,
                Color.Cyan, Color.Magenta, Color(0xFFFB8C00),
                Color(0xFF6200EA), Color(0xFF00BFA5), Color(0xFFFF6F00),
                Color(0xFF311B92), Color(0xFF263238), Color(0xFFECEFF1)
            )
            colors.forEach { colorOption ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(colorOption, CircleShape)
                        .border(2.dp, if (selectedColor == colorOption) Color.Black else Color.Transparent, CircleShape)
                        .clickable { onColorChange(colorOption) }
                )
            }
        }
    }
}

@Composable
fun TextStyleSelector(
    selectedTextColor: Color,
    selectedFontFamily: String,
    selectedFontSize: Int,
    selectedFontWeight: String,
    onTextColorChange: (Color) -> Unit,
    onFontFamilyChange: (String) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onFontWeightChange: (String) -> Unit,
    title: String = "Yazı Stili"
) {
    Column {
        Text(title)
        Spacer(modifier = Modifier.height(8.dp))

        // Text Color Selector
        Text("Yazı Rengi")
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            val textColors = listOf(
                Color.White, Color.Black, Color.Gray, Color.Red, Color.Green,
                Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta,
                Color(0xFFFB8C00), Color(0xFFE0E0E0)
            )
            textColors.forEach { colorOption ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(colorOption, CircleShape)
                        .border(
                            2.dp,
                            if (selectedTextColor == colorOption) Color.Black else Color.Transparent,
                            CircleShape
                        )
                        .clickable { onTextColorChange(colorOption) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Font Family Selector
        Text("Yazı Tipi")
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow {
            val fontFamilies = listOf("Default", "Serif", "SansSerif", "Monospace", "Cursive")
            items(fontFamilies) { font ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            if (selectedFontFamily == font) Color(0xFFFB8C00) else Color.LightGray,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onFontFamilyChange(font) }
                        .padding(8.dp)
                ) {
                    Text(
                        text = font,
                        fontFamily = when (font) {
                            "Serif" -> FontFamily.Serif
                            "SansSerif" -> FontFamily.SansSerif
                            "Monospace" -> FontFamily.Monospace
                            "Cursive" -> FontFamily.Cursive
                            else -> FontFamily.Default
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Font Size Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Yazı Boyutu: ${selectedFontSize}sp")
            Spacer(modifier = Modifier.padding(8.dp))
            LazyRow {
                val fontSizes = listOf(12, 14, 16, 18, 20, 22, 24)
                items(fontSizes) { size ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                if (selectedFontSize == size) Color(0xFFFB8C00) else Color.LightGray,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onFontSizeChange(size) }
                            .padding(8.dp)
                    ) {
                        Text(text = "$size")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

// Font Weight Selector
        Text("Yazı Kalınlığı")
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow {
            val fontWeights = listOf("Normal", "Bold", "Light", "Medium")
            items(fontWeights) { weight ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            if (selectedFontWeight == weight) Color(0xFFFB8C00) else Color.LightGray,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onFontWeightChange(weight) }
                        .padding(8.dp)
                ) {
                    Text(
                        text = weight,
                        fontWeight = when (weight) {
                            "Bold" -> FontWeight.Bold
                            "Light" -> FontWeight.Light
                            "Medium" -> FontWeight.Medium
                            else -> FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}