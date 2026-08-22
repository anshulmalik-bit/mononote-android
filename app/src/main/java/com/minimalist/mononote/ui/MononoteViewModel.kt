package com.minimalist.mononote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.minimalist.mononote.data.NoteEntity
import com.minimalist.mononote.data.NoteRepository
import com.minimalist.mononote.live.LiveCardManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MononoteViewModel(
    private val repository: NoteRepository,
    private val liveCardManager: LiveCardManager
) : ViewModel() {

    val activeNote: StateFlow<NoteEntity> = repository.activeNoteFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NoteEntity(isActive = true)
        )

    val archivedNotes: StateFlow<List<NoteEntity>> = repository.archivedNotesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _showArchiveSheet = MutableStateFlow(false)
    val showArchiveSheet: StateFlow<Boolean> = _showArchiveSheet.asStateFlow()

    private var autoSaveJob: Job? = null

    init {
        // Automatically sync Live Card with active note changes
        viewModelScope.launch {
            activeNote.collectLatest { note ->
                if (note.isLive) {
                    liveCardManager.updateLiveCard(note)
                }
            }
        }
    }

    fun onContentChange(newContent: String) {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(100) // 100ms debounce for rapid keystrokes
            repository.saveActiveNoteContent(newContent)
        }
    }

    fun appendVoiceNote(voiceText: String) {
        viewModelScope.launch {
            repository.appendToActiveNote(voiceText)
        }
    }

    fun toggleLiveStatus() {
        viewModelScope.launch {
            val current = repository.getActiveNoteDirect()
            val newLive = !current.isLive
            repository.setLiveStatus(newLive)
            if (newLive) {
                liveCardManager.updateLiveCard(current.copy(isLive = true))
            } else {
                liveCardManager.dismissLiveCard()
            }
        }
    }

    fun cycleFontStyle() {
        viewModelScope.launch {
            val current = repository.getActiveNoteDirect()
            val nextStyle = when (current.fontStyle) {
                "sans" -> "mono"
                "mono" -> "serif"
                else -> "sans"
            }
            repository.setFontStyle(nextStyle)
        }
    }

    fun archiveAndClear() {
        viewModelScope.launch {
            repository.archiveActiveNote()
            liveCardManager.dismissLiveCard()
        }
    }

    fun restoreArchivedNote(id: Long) {
        viewModelScope.launch {
            repository.restoreArchivedNote(id)
            _showArchiveSheet.value = false
        }
    }

    fun deleteArchivedNote(id: Long) {
        viewModelScope.launch {
            repository.deleteArchivedNote(id)
        }
    }

    fun setArchiveSheetVisible(visible: Boolean) {
        _showArchiveSheet.value = visible
    }
}

class MononoteViewModelFactory(
    private val repository: NoteRepository,
    private val liveCardManager: LiveCardManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MononoteViewModel(repository, liveCardManager) as T
    }
}
