package com.rizal.post

import androidx.room.*

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    suspend fun getAll(): List<PostEntity>

    @Insert
    suspend fun insert(post: PostEntity)

    @Update
    suspend fun update(post: PostEntity)

    @Delete
    suspend fun delete(post: PostEntity)
}
