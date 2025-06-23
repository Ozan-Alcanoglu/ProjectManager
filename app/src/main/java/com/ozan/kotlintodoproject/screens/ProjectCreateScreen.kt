package com.ozan.kotlintodoproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavHostController
import com.ozan.kotlintodoproject.ui.components.FormDropdownField

import com.ozan.kotlintodoproject.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCreateScreen(
    sharedViewModel: SharedViewModel= hiltViewModel(),

    onBack: () -> Unit,
    onSave: () -> Unit,
    navController :NavHostController
) {

//    val state by viewModel.state




    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text("Proje Ekle")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .background(Color(0xFFF5F5F5))
        ) {

            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var priority by remember { mutableStateOf("") }

            OutlinedTextField(
                value = title,
                onValueChange = { newText -> title = newText },
                label = { Text("Proje ismini girin", color = Color.Black) },
                singleLine = true,
                shape = RoundedCornerShape(
                    topEnd = 8.dp,
                ),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = description,
                onValueChange = { newText -> description = newText },
                label = { Text("Proje açıklamasını girin", color = Color.Black) },
                singleLine = true,
                shape = RoundedCornerShape(
                    topEnd = 8.dp,
                ),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))



            Spacer(modifier = Modifier.height(16.dp))


            FormDropdownField(
                value = priority,
                onValueChange = { newText-> priority=newText },
                label = "Proje Önceliği",
                options = listOf(
                    "0" to "DÜŞÜK",
                    "1" to "ORTA",
                    "2" to "YÜKSEK"
                ),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            val isFormValid = title.isNotBlank() &&
                priority.isNotBlank()

            Button(onClick = {
                sharedViewModel.saveProjectData(
                    titleD = title,
                    descriptionD = description,
                    priorityD = priority.toIntOrNull() ?: 0,
                )
                navController.navigate("project_detail")
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4300CC))
            ) {
                Text("Devam Et", color = Color.White)
            }


//            state.error?.let { error ->
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = error,
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
        }
    }
}

