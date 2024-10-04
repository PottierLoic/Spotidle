package com.example.spotidle.ui.guess

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spotidle.R
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold

@Composable
fun MusicGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val correctSongName = "Ratio" // TODO REMOVE
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.testsong) }
    var isPlaying by remember { mutableStateOf(false) }

    SpotifightScaffold(navController = navController) {
        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color(0xFF1ED760))
        ) {
            Button(
                onClick = {
                    isPlaying = !isPlaying;
                    if (!isPlaying) mediaPlayer.pause() else mediaPlayer.start();
                },
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow, // TODO: change icon to pause icon
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(correctGuessName = correctSongName)
    }
}
