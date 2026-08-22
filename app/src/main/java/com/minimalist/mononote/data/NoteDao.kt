package com.minimalist.mononote.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isActive = 1 LIMIT 1")
    fun getActiveNoteFlow(): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveNoteDirect(): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE isActive = 0 ORDER BY updatedAt DESC")
    fun getArchivedNotesFlow(): Flow<List<NoteEntity>>

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM notes WHERE isActive = 0")
    suspend fun clearAllArchived()

    @Query("UPDATE notes SET isActive = 0, isLive = 0, updatedAt = :timestamp WHERE isActive = 1")
    suspend fun markActiveAsArchived(timestamp: Long)

    @Query("UPDATE notes SET isLive = :isLive WHERE isActive = 1")
    suspend fun setLiveStatus(isLive: Boolean)

    @Query("UPDATE notes SET fontStyle = :fontStyle WHERE isActive = 1")
    suspend fun setFontStyle(fontStyle: String)

    @Transaction
    suspend fun archiveAndCreateNew(timestamp: Long = System.currentTimeMillis()): Long {
        markActiveAsArchived(timestamp)
        return insert(NoteEntity(isActive = true, content = "", updatedAt = timestamp))
    }

    @Transaction
    suspend fun restoreArchivedAsActive(archivedId: Long, timestamp: Long = System.currentTimeMillis()) {
        markActiveAsArchived(timestamp)
        // Mark selected as active
        updateActiveStatus(archivedId, isActive = true, timestamp = timestamp)
    }

    @Query("UPDATE notes SET isActive = :isActive, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean, timestamp: Long)
}
