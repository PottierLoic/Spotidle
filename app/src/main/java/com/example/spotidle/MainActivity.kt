package com.example.spotidle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.spotidle.ui.theme.SpotidleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone // used for music guess
import androidx.compose.material.icons.filled.Star // used for lyrics guess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AddCircle // used for album guess
import androidx.compose.material.icons.filled.Person // used for artist guess
import androidx.compose.ui.graphics.Color

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
    var selectedItem by remember { mutableStateOf(0) }

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
        when (selectedItem) {
            0 -> MusicGuessScreen(modifier = Modifier.padding(innerPadding))
            1 -> LyricsGuessScreen(modifier = Modifier.padding(innerPadding))
            2 -> HomeScreen(modifier = Modifier.padding(innerPadding))
            3 -> AlbumGuessScreen(modifier = Modifier.padding(innerPadding))
            4 -> ArtistGuessScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<String>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.Transparent
    ) {
        items.forEachIndexed { index, item ->
            val icon = when (index) {
                0 -> Icons.Default.Phone // Music Guess icon
                1 -> Icons.Default.Star // Lyrics Guess icon
                2 -> Icons.Default.Home // Home icon
                3 -> Icons.Default.AddCircle // Album Guess icon
                4 -> Icons.Default.Person // Artist Guess icon
                else -> Icons.Default.Home
            }

            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = item) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index)},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFF1ED760)
                )

            )
        }
    }
}

@Composable
fun MusicGuessScreen(modifier: Modifier = Modifier) {
    Text(text = "", modifier = modifier.fillMaxSize())
}

@Composable
fun LyricsGuessScreen(modifier: Modifier = Modifier) {
    Text(text = "", modifier = modifier.fillMaxSize())
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Text(text = "", modifier = modifier.fillMaxSize())
}

@Composable
fun AlbumGuessScreen(modifier: Modifier = Modifier) {
    Text(text = "", modifier = modifier.fillMaxSize())
}

@Composable
fun ArtistGuessScreen(modifier: Modifier = Modifier) {
    Text(text = "", modifier = modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SpotidleTheme {
        MainScreen()
    }
}
