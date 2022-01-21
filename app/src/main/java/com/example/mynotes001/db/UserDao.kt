package com.example.mynotes001.db

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM User where name = :userName and password = :password")
    fun getUser(
        userName: String?,
        password: String?
    ): User?

    @Query("SELECT * FROM User where name = :userName ")
    fun isUser(
        userName: String?
    ): User?

    @Query("SELECT password FROM User where name = :name ")
    fun password(
        name: String?
    ): String?
    @Query("Update User SET password= :password where name = :name ")
    fun passwordUpdate(
        name: String?,
        password: String?
    )

    @Query("SELECT * FROM User")
    fun getAll(): User?

    @Insert
    fun addUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}