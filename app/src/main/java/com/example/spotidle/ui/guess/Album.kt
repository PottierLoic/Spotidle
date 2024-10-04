package com.example.spotidle.ui.guess

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spotidle.R
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold

@Composable
fun AlbumGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val correctAlbumName = "Quand la musique est bonne" // TODO REMOVE
    var blurAmount by remember { mutableFloatStateOf(25f) }

    SpotifightScaffold(navController = navController) {
        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.jjgalbumcover),
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
            correctGuessName = correctAlbumName,
            onGuessSubmit = { guess ->
                if (guess.equals(correctAlbumName, ignoreCase = true)) {
                    blurAmount = 0f
                } else {
                    blurAmount = (blurAmount - 5f).coerceAtLeast(0f)
                }
            }
        )
    }
}