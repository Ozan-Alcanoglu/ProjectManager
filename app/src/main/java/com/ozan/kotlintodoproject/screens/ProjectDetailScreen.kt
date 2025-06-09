package com.ozan.kotlintodoproject.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozan.kotlintodoproject.ui.components.FormTextField

import com.ozan.kotlintodoproject.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

@Immutable
data class NestedTextField(
    val id: Int,
    val text: String,
    val children: List<NestedTextField> = emptyList(),
    val isExpanded: Boolean = true
) {
    fun updateText(newText: String): NestedTextField {
        return copy(text = newText)
    }

    fun addChild(newChild: NestedTextField): NestedTextField {
        return copy(children = children + newChild)
    }

    fun toggleExpanded(): NestedTextField {
        return copy(isExpanded = !isExpanded)
    }
}

private fun updateItemInList(
    items: List<NestedTextField>,
    itemId: Int,
    update: (NestedTextField) -> NestedTextField
): MutableList<NestedTextField> {
    return items.map { item ->
        if (item.id == itemId) {
            update(item)
        } else {
            item.copy(children = updateItemInList(item.children, itemId, update))
        }
    }.toMutableList()
}

@Composable
fun NestedTextFieldItem(
    item: NestedTextField,
    onAddChild: (NestedTextField) -> Unit,
    onTextChange: (NestedTextField, String) -> Unit,
    level: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp)
    ) {
        if (level > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-16).dp)
                    .padding(end = 8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = "Child item indicator",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }
        }

        val labelText = if (level == 0) "Ana Görev" else "Alt Görev"

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FormTextField(
                value = item.text,
                onValueChange = { onTextChange(item, it) },
                label = labelText,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                singleLine = true
            )

            if (level == 0) {
                IconButton(
                    onClick = { onAddChild(item) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add child",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(32.dp))
            }
        }


        if (item.isExpanded) {
            item.children.forEach { child ->
                NestedTextFieldItem(
                    item = child,
                    onAddChild = onAddChild,
                    onTextChange = onTextChange,
                    level = level + 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetail(
    sharedViewModel: SharedViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSave: () -> Unit,
    navController: NavHostController
) {
    var counter by remember { mutableStateOf(0) }

    LaunchedEffect (Unit) {
        sharedViewModel.updateItems(emptyList())
        counter = 0
    }

    val scrollState = rememberScrollState()
    val items by sharedViewModel.nestedTextFields.collectAsState()
//
//
//    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Projeyi Detaylandırın")
                },
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
                            sharedViewModel.updateItems(items + NestedTextField(id = counter++, text = ""))

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
                    containerColor = Color(0xFF00E0CC),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Projenizin adımlarınızı belirleyiniz",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )

            items.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    NestedTextFieldItem(
                        item = item,
                        onAddChild = { parent ->
                            val newItems = items.map {
                                if (it.id == parent.id) it.addChild(NestedTextField(id = counter++, text = ""))
                                else it
                            }
                            sharedViewModel.updateItems(newItems)
                        }
                        ,
                        onTextChange = { updatedItem, newText ->
                            val updatedItems = updateItemInList(sharedViewModel.items, updatedItem.id) {
                                it.updateText(newText)
                            }
                            sharedViewModel.updateItems(updatedItems)
                        }
                        ,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val projectResult = sharedViewModel.addProject(
                                title = sharedViewModel.title,
                                description = sharedViewModel.description,
                                priority = sharedViewModel.priority
                            )

                            projectResult.onSuccess { project ->
                                val projectId = project.id

                                items.forEach { rootItem ->
                                    val branchResult = sharedViewModel.addBranch(rootItem.text)

                                    branchResult.onSuccess { branch ->
                                        val mainTaskResult = sharedViewModel.addTask(
                                            projectId = projectId,
                                            branchId = branch.id,
                                            taskname = rootItem.text,
                                            parentId = null
                                        )

                                        mainTaskResult.onSuccess { mainTask ->
                                            rootItem.children.forEach { childItem ->
                                                val childTaskResult = sharedViewModel.addTask(
                                                    projectId = projectId,
                                                    branchId = branch.id,
                                                    taskname = childItem.text,
                                                    parentId = mainTask.id
                                                )

                                                childTaskResult.onSuccess { childTask ->
                                                    childItem.children.forEach { subChildItem ->
                                                        sharedViewModel.addTask(
                                                            projectId = projectId,
                                                            branchId = branch.id,
                                                            taskname = subChildItem.text,
                                                            parentId = childTask.id
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }.onFailure { e ->
                                        Log.e("SaveProject", "Branch eklenirken hata: ${e.message}")
                                    }
                                }

                                onSave()
                            }.onFailure { e ->
                                Log.e("SaveProject", "Proje kaydedilirken hata: ${e.message}")
                            }
                        } catch (e: Exception) {
                            Log.e("SaveProject", "Beklenmeyen hata: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E0CC))
            ) {
                Text("Kaydet")
            }


        }
    }
}
@Preview(showBackground = true)
@Composable
fun Test() {


}
