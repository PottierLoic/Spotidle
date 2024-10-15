package com.example.spotidle

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class MainViewModel : ViewModel() {
    var isSpotifyConnected = mutableStateOf(false)
    var username = mutableStateOf("")
    var fourRandTracksId = mutableStateOf(emptyList<String>())
    var tracksSuggestions: List<String> = emptyList()
    var albumsSuggestions: List<String> = emptyList()
    var artistsSuggestions: List<String> = emptyList()
}