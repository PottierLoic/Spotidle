package com.example.spotidle.ui.guess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.home.components.SpotifightTitle

@Composable
fun ArtistGuessScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val correctArtistName = "TO REPLACE" // TODO REMOVE

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color(0xFF1ED760))
        ) {
            // TODO artist content here
        }

        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(correctGuessName=correctArtistName)
    }
}