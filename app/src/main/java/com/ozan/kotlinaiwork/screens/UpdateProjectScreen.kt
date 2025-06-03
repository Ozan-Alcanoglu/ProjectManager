package com.ozan.kotlinaiwork.screens

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import com.ozan.kotlinaiwork.viewmodel.ProjectViewModel


@Composable
fun UpdateProject(
    projectViewModel: ProjectViewModel
){
    projectViewModel.currentProject?.let { Text(it.title) }
}
