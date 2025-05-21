package com.ozan.kotlinaiwork.di

import com.ozan.kotlinaiwork.repository.ProjectRepository
import com.ozan.kotlinaiwork.repository.ProjectRepositoryImpl
import com.ozan.kotlinaiwork.repository.TaskRepository
import com.ozan.kotlinaiwork.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    
    @Binds
    @ViewModelScoped
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository
    
    @Binds
    @ViewModelScoped
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}
