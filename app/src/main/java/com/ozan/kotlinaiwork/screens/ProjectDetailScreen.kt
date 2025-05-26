package com.ozan.kotlinaiwork.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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

data class NestedTextField(
    val id: Int,
    var text: String,
    val children: MutableList<NestedTextField> = mutableListOf(),
    var isExpanded: Boolean = true
)

@Composable
fun NestedTextFieldItem(
    item: NestedTextField,
    onAddChild: (NestedTextField) -> Unit,
    onTextChange: (String) -> Unit,
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
                    .offset(x = (-16).dp) // negatif offset ile sola kaydırma
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
                onValueChange = onTextChange,
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

        // Render children if expanded
        if (item.isExpanded) {
            item.children.forEach { child ->
                NestedTextFieldItem(
                    item = child,
                    onAddChild = onAddChild,
                    onTextChange = { newText ->
                        child.text = newText
                    },
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

    var items by remember {
        mutableStateOf(mutableListOf<NestedTextField>())
    }

    val state by viewModel.state



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.material3.Text(if (projectId == null) Strings.ADD_PROJECT else Strings.EDIT_PROJECT)
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
                            items = items.toMutableList().apply {
                                add(NestedTextField(
                                    id = counter++,
                                    text = ""
                                ))
                            }
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
                            items = items.map {
                                if (it.id == parent.id) {
                                    it.copy(children = it.children.toMutableList().apply {
                                        add(NestedTextField(
                                            id = counter++,
                                            text = ""
                                        ))
                                    })
                                } else it
                            }.toMutableList()
                        },
                        onTextChange = { newText ->
                            items = items.map {
                                if (it.id == item.id) it.copy(text = newText) else it
                            }.toMutableList()
                        },
                        modifier = Modifier.padding(8.dp),

                    )
                }
            }


        }
    }
}
@Preview(showBackground = true)
@Composable
fun Test() {



}
