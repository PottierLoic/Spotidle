package com.example.spotidle.ui.guess

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.spotidle.TrackInfo
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold

@SuppressLint("AutoboxingStateCreation")
@Composable
fun AlbumGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    track: TrackInfo
) {
    val context = LocalContext.current
    var attempts by remember { mutableIntStateOf(0) }
    var blurAmount by remember { mutableFloatStateOf(25f) }


    SpotifightScaffold(navController = navController) {
        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color.Transparent)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = track.albumCoverUrl)
                        .apply {
                            crossfade(true)
                            scale(Scale.FILL)
                        }.build()
                ),
                contentDescription = "Album Cover",
                modifier = Modifier
                    .blur(radius = blurAmount.dp)
                    .padding(end = 8.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(
            correctGuessName = track.album,
            attempts = attempts,
            onGuessSubmit = { guess ->
                if (guess.equals(track.album, ignoreCase = true)) {
                    blurAmount = 0f
                } else {
                    attempts += 1

                    if (attempts < 4) {
                        blurAmount = (blurAmount - 5f).coerceAtLeast(0f)
                    } else if (attempts == 4) {
                        blurAmount = 0f
                    }
                }
            },
            toGuess = "album"
        )
    }
}
