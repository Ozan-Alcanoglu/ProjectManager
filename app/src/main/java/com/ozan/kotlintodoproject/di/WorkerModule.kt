package com.ozan.kotlintodoproject.di

import androidx.work.WorkerFactory
import androidx.hilt.work.HiltWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {
    @Binds
    @Singleton
    abstract fun bindWorkerFactory(factory: HiltWorkerFactory): WorkerFactory
}
