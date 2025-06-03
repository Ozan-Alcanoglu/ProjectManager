package com.ozan.kotlinaiwork.screens

import android.annotation.SuppressLint
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
    projectViewModel: ProjectViewModel= hiltViewModel()
){

    val tasks by projectViewModel.tasks.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.material3.Text("Projeyi Düzenleyin")
                },
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
                            //sharedViewModel.updateItems(items + NestedTextField(id = counter++, text = ""))

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
    ){padding->

        LaunchedEffect(projectViewModel.currentProject?.id) {
            projectViewModel.currentProject?.id?.let { id ->
                projectViewModel.loadTasksByProjectId(id)
            }
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(padding))
        {

            projectViewModel.currentProject?.title?.let { title ->
                Text(
                    text = title,
                    fontSize = 40.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )
            }

            projectViewModel.currentProject?.description?.let { description->
                Text(
                    text = description,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)

                )
            }


            tasks.forEach { task ->
                task.taskname?.let { name ->
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                    )
                }
            }

        }

    }
}
