package com.example.spotidle.ui.guess

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold

@Composable
fun MusicGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String
) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var attempts by remember { mutableIntStateOf(0) }
    var winState by remember { mutableStateOf(false) }
    var correctTrackName by remember { mutableStateOf("") }
    var trackPreviewUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=dQw4w9WgXcQ") }
    var albumCoverUrl by remember { mutableStateOf("") }

    LaunchedEffect(trackPreviewUrl) {
        trackPreviewUrl.let {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(it)
                prepareAsync()
                setOnPreparedListener {
                    Log.d("MediaPlayer", "MediaPlayer started")
                }
            }
        }
    }

    SpotifightScaffold(navController = navController) {
        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color(0xFF1ED760))
        ) {
            if(winState) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = albumCoverUrl)
                            .apply {
                                crossfade(true)
                                scale(Scale.FILL)
                            }.build()
                    ),
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )
            }
            Button(
                onClick = {
                    isPlaying = !isPlaying;
                    if (!isPlaying) mediaPlayer?.pause() else mediaPlayer?.start();
                },
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(
            correctGuessName = correctTrackName,
            toGuess = "song",
            onGuessSubmit = { guess ->
                if (guess.equals(correctTrackName, ignoreCase = true)) {
                    winState = true
                } else {
                    attempts += 1
                }
            },
            attempts = attempts,
            winState = winState
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
