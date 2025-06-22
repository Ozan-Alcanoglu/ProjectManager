package com.ozan.kotlintodoproject.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlintodoproject.viewmodel.ProjectViewModel
import com.ozan.kotlintodoproject.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProject(
    onBack: () -> Unit,
    sharedViewModel: SharedViewModel = hiltViewModel(),
    projectViewModel: ProjectViewModel = hiltViewModel()
) {
    val tasks by projectViewModel.tasks.collectAsState()
    val checkboxStates = remember { mutableStateMapOf<String, Boolean>() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projeyi Düzenleyin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Proje Sil"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00E0CC),
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Projeyi Sil") },
                text = { Text("Projeyi silmek istediğinize emin misiniz?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            projectViewModel.currentProject?.id?.let { id ->
                                projectViewModel.deleteProjectById(id)
                                projectViewModel.deleteAllTasksForProject(id)
                            }
                            showDeleteDialog = false
                            onBack()
                        }
                    ) {
                        Text("Evet")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Hayır")
                    }
                }
            )
        }

        LaunchedEffect(projectViewModel.currentProject?.id) {
            projectViewModel.currentProject?.id?.let { id ->
                projectViewModel.loadTasksByProjectId(id)
            }
        }

        LaunchedEffect(tasks) {
            tasks.forEach { task ->
                task.id?.let { taskId ->
                    checkboxStates[taskId] = task.isDone == true
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            projectViewModel.currentProject?.title?.let { title ->
                Text(
                    text = title,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 20.dp)
                )
            }

            projectViewModel.currentProject?.priority?.let { priority ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Önem seviyesi: " + when (priority) {
                            2 -> "YÜKSEK"
                            1 -> "ORTA"
                            else -> "DÜŞÜK"
                        },
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 12.dp, top = 10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (priority) {
                                    2 -> Color(0xFFD21400)
                                    1 -> Color(0xFFFFD166)
                                    else -> Color(0xFF76FF03)
                                }
                            )
                            .padding(14.dp)
                    )
                }
            }



            projectViewModel.currentProject?.description?.let { description ->
                Text(
                    text = "Açıklama:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 30.dp)
                )
                Text(
                    text = "-$description",
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "Görevler:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 8.dp)
            )
            
            val groupedTasks = tasks.groupBy { it.parentId }

            tasks.filter { it.parentId == null }.forEach { parentTask ->
                val isChecked = checkboxStates[parentTask.id ?: ""] ?: false

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "▶ " + (parentTask.taskname ?: ""),
                        fontSize = 30.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            parentTask.id?.let { parentId ->
                                checkboxStates[parentId] = checked
                                val subtasks = groupedTasks[parentId] ?: emptyList()
                                subtasks.forEach { subtask ->
                                    subtask.id?.let { checkboxStates[it] = checked }
                                }
                            }
                        }
                    )
                }

                groupedTasks[parentTask.id]?.forEach { subtask ->
                    val isSubChecked = checkboxStates[subtask.id ?: ""] ?: false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 36.dp, end = 12.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Alt Görev Oku",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = subtask.taskname ?: "",
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = isSubChecked,
                            onCheckedChange = { checked ->
                                subtask.id?.let { checkboxStates[it] = checked }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val context = LocalContext.current

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            checkboxStates.forEach { (taskId, isDone) ->
                                projectViewModel.updateTaskIsDone(taskId, isDone)
                            }
                            Toast.makeText(context, "Proje güncellendi", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Hata oluştu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E0CC))

            ) {
                Text("Kaydet", color = Color.Black)
            }

        }
    }
}
