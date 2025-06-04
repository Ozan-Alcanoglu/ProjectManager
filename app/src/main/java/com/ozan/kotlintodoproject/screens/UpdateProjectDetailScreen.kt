package com.ozan.kotlintodoproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlintodoproject.viewmodel.ProjectViewModel
import com.ozan.kotlintodoproject.viewmodel.SharedViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProjectDetail(
    onBack: () -> Unit,
    sharedViewModel: SharedViewModel= hiltViewModel(),
    projectViewModel: ProjectViewModel= hiltViewModel()
){
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
                    containerColor = Color(0xFF76FF03), // Light green color
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ){padding->

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(padding))
        {

            projectViewModel.currentProject?.title?.let {title->  Text(title,fontSize = 24.sp)}

        }

    }
}

