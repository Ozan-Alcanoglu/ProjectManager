package com.ozan.kotlintodoproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ozan.kotlintodoproject.ui.components.FormTextField

//@Immutable
//data class UpdateNestedTextField(
//    val id: Int,
//    val text: String,
//    val children: List<NestedTextField> = emptyList(),
//    val isExpanded: Boolean = true
//) {
//    fun updateText(newText: String): NestedTextField {
//        return copy(text = newText)
//    }
//
//    fun addChild(newChild: NestedTextField): NestedTextField {
//        return copy(children = children + newChild)
//    }
//
//    fun toggleExpanded(): NestedTextField {
//        return copy(isExpanded = !isExpanded)
//    }
//}
//
//private fun updateItemInList(
//    items: List<NestedTextField>,
//    itemId: Int,
//    update: (NestedTextField) -> NestedTextField
//): MutableList<NestedTextField> {
//    return items.map { item ->
//        if (item.id == itemId) {
//            update(item)
//        } else {
//            item.copy(children = updateItemInList(item.children, itemId, update))
//        }
//    }.toMutableList()
//}
//
//@Composable
//fun UpdateNestedTextFieldItem(
//    item: NestedTextField,
//    onAddChild: (NestedTextField) -> Unit,
//    onTextChange: (NestedTextField, String) -> Unit,
//    level: Int = 0,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(start = (level * 16).dp)
//            .background(Color(0xFFF5F5F5))
//    ) {
//        if (level > 0) {
//            Box(
//                modifier = Modifier
//                    .offset(x = (-16).dp)
//                    .padding(end = 8.dp)
//                    .size(24.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowRight,
//                    contentDescription = "Child item indicator",
//                    tint = MaterialTheme.colorScheme.outline,
//                    modifier = Modifier
//                        .size(64.dp)
//                        .align(Alignment.Center)
//                )
//            }
//        }
//
//        val labelText = if (level == 0) "Ana Görev" else "Alt Görev"
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            FormTextField(
//                value = item.text,
//                onValueChange = { onTextChange(item, it) },
//                label = labelText,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp),
//                singleLine = true
//            )
//
//            if (level == 0) {
//                IconButton(
//                    onClick = { onAddChild(item) },
//                    modifier = Modifier
//                        .size(32.dp)
//                        .clip(CircleShape)
//                        .background(Color.White.copy(alpha = 0.3f))
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = "Add child",
//                        tint = Color.Black,
//                        modifier = Modifier.size(16.dp)
//                    )
//                }
//            } else {
//                Spacer(modifier = Modifier.size(32.dp))
//            }
//        }
//
//
//        if (item.isExpanded) {
//            item.children.forEach { child ->
//                NestedTextFieldItem(
//                    item = child,
//                    onAddChild = onAddChild,
//                    onTextChange = onTextChange,
//                    level = level + 1
//                )
//            }
//        }
//    }
//}

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlintodoproject.model.Task
import com.ozan.kotlintodoproject.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckboxClick: (Boolean) -> Unit
) {
    androidx.compose.material.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Checkbox(
                checked = task.isDone ?: false,
                onCheckedChange = onCheckboxClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = task.taskname ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProjectDetailScreen(
    projectId: String?,
    onBack: () -> Unit,
    projectViewModel: ProjectViewModel = hiltViewModel()
) {
    val tasks by projectViewModel.tasks.collectAsState()

    LaunchedEffect(projectId) {
        projectId?.let { id ->
            projectViewModel.loadTasksByProjectId(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material3.Text("Görevleri Düzenleyin") },
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
                        onClick = {},
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Görev Ekle"
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
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = {  },
                        onCheckboxClick = { isChecked ->
                            task.id?.let { taskId ->
                                projectViewModel.updateTaskIsDone(taskId, isChecked)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))


            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4300CC))
            ) {
                androidx.compose.material3.Text("Kaydet", color = Color.White)
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun TestThisScreen(){

}
