package com.ozan.kotlinaiwork.screens.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavHostController
import com.ozan.kotlinaiwork.ui.components.FormDropdownField
import com.ozan.kotlinaiwork.ui.components.FormTextField
import com.ozan.kotlinaiwork.ui.theme.Strings
import com.ozan.kotlinaiwork.viewmodel.ProjectEditEvent
import com.ozan.kotlinaiwork.viewmodel.ProjectEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditScreen(
    viewModel: ProjectEditViewModel = hiltViewModel(),
    projectId: String? = null,
    onBack: () -> Unit,
    onSave: () -> Unit,
    navController :NavHostController
) {

    val state by viewModel.state


    LaunchedEffect(projectId) {
        projectId?.let { viewModel.loadProject(it) }
    }


    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ProjectEditEvent.TitleChanged -> { /* ViewModel'de işleniyor */ }
                is ProjectEditEvent.DescriptionChanged -> { /* ViewModel'de işleniyor */ }
                is ProjectEditEvent.CategoryChanged -> { /* ViewModel'de işleniyor */ }
                is ProjectEditEvent.PriorityChanged -> { /* ViewModel'de işleniyor */ }
                ProjectEditEvent.Save -> onSave()
                is ProjectEditEvent.ShowMessage -> {
                    // TODO: Snackbar göster
                }
            }
        }
    }

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
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Başlık alanı
            FormTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(ProjectEditEvent.TitleChanged(it)) },
                label = Strings.PROJECT_TITLE,
                isError = state.titleError != null,
                errorMessage = state.titleError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Açıklama alanı
            FormTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(ProjectEditEvent.DescriptionChanged(it)) },
                label = Strings.PROJECT_DESCRIPTION,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kategori seçimi
            FormTextField(
                value = state.category,
                onValueChange = { viewModel.onEvent(ProjectEditEvent.CategoryChanged(it)) },
                label = Strings.PROJECT_CATEGORY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Öncelik seçimi
            FormDropdownField(
                value = state.priority,
                onValueChange = { viewModel.onEvent(ProjectEditEvent.PriorityChanged(it)) },
                label = Strings.PROJECT_PRIORITY,
                options = listOf(
                    "0" to Strings.PRIORITY_LOW,
                    "1" to Strings.PRIORITY_MEDIUM,
                    "2" to Strings.PRIORITY_HIGH
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            val isFormValid = state.title.isNotBlank() &&
                state.category.isNotBlank() &&
                state.priority.isNotBlank()

            Button(onClick = {
                navController.navigate("project_detail")
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Devam Et")
            }

            // Hata mesajı
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

