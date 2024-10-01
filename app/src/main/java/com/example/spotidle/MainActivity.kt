package com.example.spotidle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.spotidle.ui.theme.SpotidleTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.spotidle.ui.guess.AlbumGuessScreen
import com.example.spotidle.ui.guess.ArtistGuessScreen
import com.example.spotidle.ui.guess.LyricsGuessScreen
import com.example.spotidle.ui.guess.MusicGuessScreen
import com.example.spotidle.ui.home.HomeScreen
import com.example.spotidle.ui.home.components.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotidleTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val items = listOf("Music Guess", "Lyrics Guess", "Home", "Album Guess", "Artist Guess")
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF191414),
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        val screenMod: Modifier = Modifier.padding(innerPadding);

        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(R.drawable.thumbs_up),
            contentDescription = "background_image",
            contentScale = ContentScale.FillBounds
        )

        when (selectedItem) {
            0 -> HomeScreen(modifier = screenMod)
            1 -> LyricsGuessScreen(modifier = screenMod)
            2 -> MusicGuessScreen(modifier = screenMod)
            3 -> AlbumGuessScreen(modifier = screenMod)
            4 -> ArtistGuessScreen(modifier = screenMod)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SpotidleTheme {
        MainScreen()
    }
}
