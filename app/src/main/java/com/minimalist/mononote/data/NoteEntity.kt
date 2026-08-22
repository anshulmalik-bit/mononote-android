package com.minimalist.mononote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String = "",
    val isActive: Boolean = true,
    val isLive: Boolean = false,
    val fontStyle: String = "sans", // "sans", "mono", "serif"
    val fontSize: Float = 18f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
