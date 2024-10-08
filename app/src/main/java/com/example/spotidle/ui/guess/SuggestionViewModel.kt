package com.example.spotidle.ui.guess

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SongSuggestionsViewModel : ViewModel() {
    var likedSongs = listOf("Song A", "Song B", "Song C", "Song D")
    var suggestions = mutableStateOf<List<String>>(emptyList())

    fun updateSuggestions(query: String) {
        if (query.isNotEmpty()) {
            suggestions.value = likedSongs.filter {
                it.contains(query, ignoreCase = true)
            }
        } else {
            suggestions.value = emptyList()
        }
    }
}
