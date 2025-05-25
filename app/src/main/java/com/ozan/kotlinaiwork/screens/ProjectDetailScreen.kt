package com.ozan.kotlinaiwork.screens

import android.R.attr.value
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.ozan.kotlinaiwork.viewmodel.ProjectEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetail(viewModel: ProjectEditViewModel = hiltViewModel(),
                  projectId: String? = null,
                  onBack: () -> Unit,
                  onSave: () -> Unit,
                  navController : NavHostController
) {

    LaunchedEffect(projectId) {
        projectId?.let { viewModel.loadProject(it) }
    }
    var projectDetail by remember { mutableStateOf("") }
    var subtopics by remember { mutableStateOf(mutableListOf<String>()) }

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
                            subtopics = subtopics.toMutableList().apply { add("") }
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

            TextField(
                value = state.title,
                onValueChange = { newValue ->
                    viewModel.onEvent(ProjectEditEvent.TitleChanged(newValue))
                },
                label = { Text("Başlık") },
                placeholder = { Text("Proje konu başlığı") },
                isError = state.titleError != null,
                modifier = Modifier.fillMaxWidth()
            )

            subtopics.forEachIndexed { index, subtopic ->
                FormTextField(
                    value = subtopic,
                    onValueChange = { newValue ->
                        subtopics[index] = newValue
                    },
                    label = "Alt Başlık ${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    subtopics.add("")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Alt Başlık Ekle")
            }


        }
    }
}
@Preview(showBackground = true)
@Composable
fun Test() {



}
