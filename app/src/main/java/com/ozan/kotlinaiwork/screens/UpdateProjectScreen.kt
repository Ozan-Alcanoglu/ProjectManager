package com.ozan.kotlinaiwork.screens

import android.annotation.SuppressLint
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.flow.forEach

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projeyi Düzenleyin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "İptal"
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
                            imageVector = Icons.Default.Add,
                            contentDescription = "Proje Ekle"
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
        ) {
            projectViewModel.currentProject?.title?.let { title ->
                Text(
                    text = title,
                    fontSize = 45.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )
            }

            projectViewModel.currentProject?.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            tasks.forEach { task ->
                val isChecked = checkboxStates[task.id ?: ""] ?: false

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.taskname ?: "",
                        fontSize = if (task.parentId == null) 30.sp else 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            task.id?.let { checkboxStates[it] = checked }
                        }
                    )
                }
            }
        }
    }
}
