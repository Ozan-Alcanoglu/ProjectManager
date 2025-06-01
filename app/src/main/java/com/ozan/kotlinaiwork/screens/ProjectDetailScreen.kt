package com.ozan.kotlinaiwork.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ozan.kotlinaiwork.ui.components.FormTextField
import com.ozan.kotlinaiwork.ui.theme.Strings
import com.ozan.kotlinaiwork.viewmodel.ProjectEditEvent
import com.ozan.kotlinaiwork.viewmodel.ProjectEditViewModel

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
    viewModel: ProjectEditViewModel= hiltViewModel(),
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
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FormTextField(
                value = item.text,
                onValueChange = { onTextChange(item, it) },
                label = "Başlık",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                singleLine = true
            )

            // Add child button
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
    viewModel: ProjectEditViewModel = hiltViewModel(),
    projectId: String? = null,
    onBack: () -> Unit,
    onSave: () -> Unit,
    navController: NavHostController
) {
    var counter by remember { mutableStateOf(0) }

    LaunchedEffect(projectId) {
        projectId?.let { viewModel.loadProject(it) }
    }

    val items = viewModel.items


    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (projectId == null) Strings.ADD_PROJECT else Strings.EDIT_PROJECT)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = Strings.CANCEL
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.updateItems(items + NestedTextField(id = counter++, text = ""))

                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = Strings.ADD_PROJECT
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(padding)
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

            // Main items list
            items.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
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
                            viewModel.updateItems(newItems)
                        }
                        ,
                        onTextChange = { updatedItem, newText ->
                            val updatedItems = updateItemInList(viewModel.items, updatedItem.id) {
                                it.updateText(newText)
                            }
                            viewModel.updateItems(updatedItems)
                        }
                        ,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                viewModel.setTasks(items)
                viewModel.onEvent(ProjectEditEvent.Save)
            },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text("Kaydet")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Test() {

    val viewModel: ProjectEditViewModel = hiltViewModel()
    val projectId: String? = null
    val onBack: () -> Unit = {}
    val onSave: () -> Unit = {}
    val navController: NavHostController = NavHostController(
        context = TODO()
    )
}
