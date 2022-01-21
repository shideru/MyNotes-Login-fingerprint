package com.example.mynotes001.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    val titlefirst: ByteArray,
    val titlesecond: ByteArray,
    val bodyfirst: ByteArray,
    val bodysecond: ByteArray
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}