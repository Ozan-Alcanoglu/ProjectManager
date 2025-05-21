package com.ozan.kotlinaiwork.repository

import kotlinx.coroutines.flow.Flow

/**
 * Tüm repository'ler için temel arayüz
 */
interface BaseRepository<T, ID> {
    suspend fun insert(item: T)
    suspend fun update(item: T)
    suspend fun delete(item: T)
    fun getById(id: ID): Flow<T?>
    fun getAll(): Flow<List<T>>
}
