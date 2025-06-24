package com.ozan.kotlintodoproject.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlintodoproject.model.Task
import com.ozan.kotlintodoproject.viewmodel.ProjectViewModel
import com.ozan.kotlintodoproject.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun TaskItem(
    task: Task,
    isSubtask: Boolean = false,
    onTaskClick: () -> Unit,
    onCheckboxClick: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onAddSubtaskClick: () -> Unit = {}
) {
    val startPadding = if (isSubtask) 56.dp else 16.dp
    val cardColors = CardDefaults.cardColors(
        containerColor = if (isSubtask) Color(0xFFEDEDED) else Color.White
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isSubtask) 2.dp else 4.dp)
            .clickable { onTaskClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSubtask) 1.dp else 2.dp),
        colors = cardColors
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(start = startPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSubtask) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Alt görev",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(35.dp)
                        .padding(end = 12.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Checkbox(
                checked = task.isDone ?: false,
                onCheckedChange = onCheckboxClick,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.padding(end = 12.dp)
            )

            Text(
                text = task.taskname ?: "",
                fontSize = if (isSubtask) 16.sp else 18.sp,
                fontWeight = if (isSubtask) FontWeight.Normal else FontWeight.SemiBold,
                color = if (task.isDone == true) Color.Gray else Color.Black,
                textDecoration = if (task.isDone == true) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )

            if (!isSubtask) {
                IconButton(
                    onClick = onAddSubtaskClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Alt görev ekle",
                        tint = Color.Black
                    )
                }
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Görevi sil",
                    tint = Color.Black
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProjectDetailScreen(
    projectId: String?,
    onBack: () -> Unit,
    projectViewModel: ProjectViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val tasks by projectViewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskName by remember { mutableStateOf("") }

    var showSubtaskDialog by remember { mutableStateOf(false) }
    var currentParentTask: Task? by remember { mutableStateOf(null) }
    var subtaskName by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(projectId) {
        projectId?.let { id ->
            projectViewModel.loadTasksByProjectId(id)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Görevleri Düzenleyin",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Görev Ekle",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4300CC),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(Color(0xFFF5F5F5))

        ) {
            val groupedTasks = tasks.groupBy { it.parentId }
            val mainTasks = tasks.filter { it.parentId == null }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(mainTasks) { mainTask ->
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        TaskItem(
                            task = mainTask,
                            isSubtask = false,
                            onTaskClick = {},
                            onCheckboxClick = { isChecked ->
                                projectViewModel.updateTaskIsDone(mainTask.id, isChecked)
                            },
                            onDeleteClick = {
                                taskToDelete = mainTask
                                showDeleteDialog = true
                            },
                            onAddSubtaskClick = {
                                currentParentTask = mainTask
                                showSubtaskDialog = true
                                subtaskName = ""
                            }
                        )

                        val subtasks = groupedTasks[mainTask.id] ?: emptyList()
                        subtasks.forEach { subtask ->
                            TaskItem(
                                task = subtask,
                                isSubtask = true,
                                onTaskClick = {},
                                onCheckboxClick = { isChecked ->
                                    projectViewModel.updateTaskIsDone(subtask.id, isChecked)
                                },
                                onDeleteClick = {
                                    taskToDelete = subtask
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4300CC))
            ) {
                Text("Kaydet", color = Color.White)
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    projectId?.let { pid ->
                        coroutineScope.launch {
                            val branchResult = sharedViewModel.addBranch(newTaskName)
                            branchResult.onSuccess { branch ->
                                sharedViewModel.addTask(
                                    projectId = pid,
                                    branchId = branch.id,
                                    taskname = newTaskName,
                                    parentId = null
                                )
                                Toast.makeText(context, "Görev eklendi", Toast.LENGTH_SHORT).show()
                                projectViewModel.loadTasksByProjectId(pid)
                            }
                        }
                    }
                }) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("İptal")
                }
            },
            title = { Text("Yeni Ana Görev") },
            text = {
                OutlinedTextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    label = { Text("Görev adı") }
                )
            }
        )
    }

    if (showSubtaskDialog) {
        AlertDialog(
            onDismissRequest = { showSubtaskDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSubtaskDialog = false
                    val parent = currentParentTask
                    val branchId = parent?.branchId
                    val pid = projectId
                    if (parent != null && branchId != null && pid != null) {
                        coroutineScope.launch {
                            sharedViewModel.addTask(
                                projectId = pid,
                                branchId = branchId,
                                taskname = subtaskName,
                                parentId = parent.id
                            )
                            Toast.makeText(context, "Alt görev eklendi", Toast.LENGTH_SHORT).show()
                            projectViewModel.loadTasksByProjectId(pid)
                        }
                    }
                }) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubtaskDialog = false }) {
                    Text("İptal")
                }
            },
            title = { Text("Alt Görev Ekle") },
            text = {
                OutlinedTextField(
                    value = subtaskName,
                    onValueChange = { subtaskName = it },
                    label = { Text("Alt görev adı") }
                )
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    taskToDelete?.let { task ->
                        coroutineScope.launch {
                            projectViewModel.deleteTask(task.id)
                            projectId?.let { projectViewModel.loadTasksByProjectId(it) }
                        }
                    }
                    showDeleteDialog = false
                }) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            },
            title = { Text("Görevi Sil") },
            text = { Text("Bu görevi silmek istediğinize emin misiniz?") }
        )
    }
}
