package com.ozan.kotlintodoproject.di

import android.content.Context
import androidx.room.Room
import com.ozan.kotlintodoproject.repository.AppDatabase
import com.ozan.kotlintodoproject.repository.BranchDao
import com.ozan.kotlintodoproject.repository.ProjectDao
import com.ozan.kotlintodoproject.repository.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "project_db"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideBranchDao(database: AppDatabase): BranchDao {
        return database.branchDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
}
