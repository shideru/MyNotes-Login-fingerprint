package com.example.mynotes001.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User (
    val name: String,
    val password: String
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}

