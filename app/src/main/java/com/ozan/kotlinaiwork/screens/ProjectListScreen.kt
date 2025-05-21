package com.ozan.kotlinaiwork.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.ui.components.ProjectItem
import com.ozan.kotlinaiwork.ui.theme.Strings
import com.ozan.kotlinaiwork.viewmodel.ProjectEvent
import com.ozan.kotlinaiwork.viewmodel.ProjectState
import com.ozan.kotlinaiwork.viewmodel.ProjectViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 80.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)
    
    if (swipeableState.offset.value < -sizePx * 0.7f) {
        LaunchedEffect(Unit) {
            onDelete()
            // Reset position after delete
            swipeableState.animateTo(0)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Silme butonu (arka plan)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Sil",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        
        // Ana içerik
        androidx.compose.material.Surface(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal
                )
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ProjectListScreen(
    onNavigate: (ProjectEvent) -> Unit,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState(emptyList())
    val uiState by viewModel.uiState.collectAsState()
    
    // UI State'i güvenli bir şekilde al
    val safeUiState = uiState ?: ProjectState.Loading

    // Projeleri yükle
    LaunchedEffect(Unit) {
        // ViewModel'den event'leri dinle
        viewModel.eventFlow.collect { event ->
            event?.let { 
                when (it) {
                    is ProjectEvent.NavigateToAddProject -> onNavigate(it)
                    is ProjectEvent.NavigateToProjectDetail -> onNavigate(it)
                    is ProjectEvent.ShowMessage -> { /* Handle message if needed */ }
                }
            }
        }
    }

    var showUndoMessage by remember { mutableStateOf<Project?>(null) }
    
    // Geri alma işlemi için zamanlayıcı
    LaunchedEffect(showUndoMessage) {
        if (showUndoMessage != null) {
            kotlinx.coroutines.delay(5000) // 5 saniye sonra geri alma mesajını kaldır
            showUndoMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.MY_HOBBY_PROJECTS) },
                actions = {
                    IconButton(
                        onClick = { onNavigate(ProjectEvent.NavigateToAddProject) },
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
        when (val state = safeUiState) {
            is ProjectState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProjectState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ProjectState.Success -> {
                if (projects.isEmpty()) {
                    EmptyState(
                        message = Strings.NO_PROJECTS_YET,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    // Geri alma mesajı
                    AnimatedVisibility(
                        visible = showUndoMessage != null,
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it }
                    ) {
                        if (showUndoMessage != null) {
                            Snackbar(
                                modifier = Modifier.padding(16.dp),
                                action = {
                                    TextButton(
                                        onClick = {
                                            showUndoMessage?.let { project ->
                                                viewModel.addProject(project)
                                            }
                                            showUndoMessage = null
                                        }
                                    ) {
                                        Text("GERİ AL")
                                    }
                                }
                            ) {
                                Text("Proje çöp kutusuna taşındı")
                            }
                        }
                    }
                    
                    // Proje listesi
                    LazyColumn(
                        contentPadding = padding,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = projects,
                            key = { it.id }
                        ) { project ->
                            SwipeToDeleteContainer(
                                onDelete = {
                                    showUndoMessage = project
                                    viewModel.deleteProject(project)
                                }
                            ) {
                                ProjectItem(
                                    project = project,
                                    onProjectClick = { projectId ->
                                        onNavigate(ProjectEvent.NavigateToProjectDetail(projectId))
                                    },
                                    onDeleteClick = {
                                        showUndoMessage = project
                                        viewModel.deleteProject(project)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            null -> {}
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
