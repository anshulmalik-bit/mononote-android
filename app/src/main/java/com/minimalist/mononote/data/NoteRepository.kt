package com.minimalist.mononote.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {

    val activeNoteFlow: Flow<NoteEntity> = noteDao.getActiveNoteFlow().map { note ->
        note ?: NoteEntity(isActive = true)
    }

    val archivedNotesFlow: Flow<List<NoteEntity>> = noteDao.getArchivedNotesFlow()

    suspend fun getActiveNoteDirect(): NoteEntity {
        return noteDao.getActiveNoteDirect() ?: run {
            val newNote = NoteEntity(isActive = true)
            val id = noteDao.insert(newNote)
            newNote.copy(id = id)
        }
    }

    suspend fun saveActiveNoteContent(content: String) {
        val current = getActiveNoteDirect()
        if (current.content != content) {
            noteDao.update(
                current.copy(
                    content = content,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun appendToActiveNote(textToAppend: String) {
        val current = getActiveNoteDirect()
        val trimmed = textToAppend.trim()
        if (trimmed.isNotBlank()) {
            val updated = if (current.content.isBlank()) {
                trimmed
            } else {
                "${current.content}\n• $trimmed"
            }
            saveActiveNoteContent(updated)
        }
    }

    suspend fun setLiveStatus(isLive: Boolean) {
        noteDao.setLiveStatus(isLive)
    }

    suspend fun setFontStyle(fontStyle: String) {
        noteDao.setFontStyle(fontStyle)
    }

    suspend fun archiveActiveNote() {
        noteDao.archiveAndCreateNew()
    }

    suspend fun restoreArchivedNote(id: Long) {
        noteDao.restoreArchivedAsActive(id)
    }

    suspend fun deleteArchivedNote(id: Long) {
        noteDao.deleteById(id)
    }

    suspend fun clearArchive() {
        noteDao.clearAllArchived()
    }
}
