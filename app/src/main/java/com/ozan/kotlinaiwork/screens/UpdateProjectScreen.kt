package com.ozan.kotlinaiwork.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlinaiwork.viewmodel.ProjectViewModel
import com.ozan.kotlinaiwork.viewmodel.SharedViewModel
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projeyi Düzenleyin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Yeni task ekleme işlemi yapılabilir
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LaunchedEffect(projectViewModel.currentProject?.id) {
            projectViewModel.currentProject?.id?.let { id ->
                projectViewModel.loadTasksByProjectId(id)
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
                    fontSize = 45.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(start = 12.dp, top = 20.dp)
                )
            }

            projectViewModel.currentProject?.description?.let { description ->
                Text(
                    text = "-$description",
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

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

            Button(
                onClick = {
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text("Kaydet")
            }
        }
    }
}
