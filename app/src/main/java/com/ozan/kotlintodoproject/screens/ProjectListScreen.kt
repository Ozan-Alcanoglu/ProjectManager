package com.ozan.kotlintodoproject.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozan.kotlintodoproject.model.Project
import com.ozan.kotlintodoproject.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    onNavigate: (String) -> Unit,
    projectViewModel: ProjectViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Bildirim izni verildi", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var priorityFilter by remember { mutableStateOf<String?>(null) }
    var dateFilter by remember { mutableStateOf<String?>(null) }

    val projectList by projectViewModel.projects.collectAsState()
    val projectTaskCompletion by projectViewModel.projectTaskCompletion.collectAsState()

    LaunchedEffect(priorityFilter, dateFilter) {
        val priorityDesc = when (priorityFilter) {
            "Yüksek" -> true
            "Düşük" -> false
            else -> null
        }

        val dateDesc = when (dateFilter) {
            "Yeni" -> true
            "Eski" -> false
            else -> null
        }

        if (priorityDesc != null && dateDesc != null) {
            projectViewModel.loadProjectsSorted(priorityDesc, dateDesc)
        } else if (priorityDesc != null) {
            if (priorityDesc) projectViewModel.loadProjectsByPriorityDesc()
            else projectViewModel.loadProjectsByPriorityAsc()
        } else if (dateDesc != null) {
            if (dateDesc) projectViewModel.loadProjectsByDateDesc()
            else projectViewModel.loadProjectsByDateAsc()
        } else {
            projectViewModel.loadProjects()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projelerim") },
                actions = {
                    IconButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!hasNotificationPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                Toast.makeText(context, "Bildirim izni zaten verilmiş", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Bildirim İkonu",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { onNavigate("add_project") }) {
                        Icon(Icons.Default.Add, contentDescription = "Yeni Proje Ekle")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16CF00),
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
                .background(Color(0xFFF5F5F5))
        ) {
            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Text(
                    text = "Hatırlatma için bildirim iznini açmalısınız.",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFCDD2))
                        .padding(12.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            FilterSection(
                priorityFilter = priorityFilter,
                onPrioritySelected = { priorityFilter = it },
                dateFilter = dateFilter,
                onDateSelected = { dateFilter = it },
                onReset = {
                    priorityFilter = null
                    dateFilter = null
                }
            )

            if (projectList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Henüz proje bulunmuyor", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Sağ üstteki + butonuna basarak yeni proje oluşturabilirsiniz",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(projectList) { project ->
                        val (done, total) = projectTaskCompletion[project.id] ?: (0 to 0)
                        val percentage = if (total > 0) (done * 100) / total else 0

                        ProjectCard(
                            project = project,
                            completionPercentage = percentage,
                            onProjectClick = {
                                projectViewModel.showProject(project.id)
                                onNavigate("update_project")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    priorityFilter: String?,
    onPrioritySelected: (String?) -> Unit,
    dateFilter: String?,
    onDateSelected: (String?) -> Unit,
    onReset: () -> Unit
) {
    var priorityExpanded by remember { mutableStateOf(false) }
    var dateExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Box {
                Text(
                    text = "Öncelik: ${priorityFilter ?: "-"}",
                    modifier = Modifier
                        .clickable { priorityExpanded = true }
                        .padding(8.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = Color.Black
                )
                DropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Yüksek") }, onClick = {
                        onPrioritySelected("Yüksek")
                        priorityExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Düşük") }, onClick = {
                        onPrioritySelected("Düşük")
                        priorityExpanded = false
                    })
                }
            }

            Box {
                Text(
                    text = "Tarih: ${dateFilter ?: "-"}",
                    modifier = Modifier
                        .clickable { dateExpanded = true }
                        .padding(8.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = Color.Black
                )
                DropdownMenu(
                    expanded = dateExpanded,
                    onDismissRequest = { dateExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Yeni") }, onClick = {
                        onDateSelected("Yeni")
                        dateExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Eski") }, onClick = {
                        onDateSelected("Eski")
                        dateExpanded = false
                    })
                }
            }
        }

        TextButton(onClick = onReset) {
            Text("Filtreyi Sıfırla")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCard(
    project: Project,
    completionPercentage: Int = 0,
    onProjectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProjectClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (project.priority) {
                            2 -> Color(0xFFD21400)
                            1 -> Color(0xFFFFD166)
                            else -> Color(0xFF76FF03)
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (project.priority) {
                        2 -> "Yüksek"
                        1 -> "Orta"
                        else -> "Düşük"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.Black,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!project.description.isNullOrBlank()) {
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "Tamamlanma: $completionPercentage%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF2196F3),
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            project.createdAt?.let { createdAt ->
                val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = formatter.format(createdAt)

                Text(
                    text = "Oluşturulma: $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
