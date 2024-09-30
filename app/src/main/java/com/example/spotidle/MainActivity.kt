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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone // used for music guess
import androidx.compose.material.icons.filled.Star // used for lyrics guess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AddCircle // used for album guess
import androidx.compose.material.icons.filled.Person // used for artist guess
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close // used for paause button
import android.media.MediaPlayer
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext

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
fun SpotifightTitle(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Spotifight",
            color = Color(0xFF1ED760),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(id = R.drawable.spotify_logo),
            contentDescription = "Spotify Logo",
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun LyricsDisplay(lyricsSnippet: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\"$lyricsSnippet\"",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuessInputField(
    label: String,
    inputText: String,
    onInputChange: (String) -> Unit,
    onGuessSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White
            )
        )
        Button(
            onClick = onGuessSubmit,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Submit Guess")
        }
    }
}
@Composable
fun GuessSection(
    modifier: Modifier = Modifier,
    correctGuessName: String,
    onGuessSubmit: (String) -> Unit = {},
) {
    var inputText by remember { mutableStateOf("") }
    val guesses = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GuessInputField(
            label = "Enter the album name",
            inputText = inputText,
            onInputChange = { inputText = it },
            onGuessSubmit = {
                if (inputText.isNotBlank()) {
                    guesses.add(inputText)
                    onGuessSubmit(inputText)
                    inputText = ""
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Previous Guesses:", color = Color.White, fontWeight = FontWeight.Bold)

        guesses.reversed().forEach { guess ->
            val isCorrect = guess.equals(correctGuessName, ignoreCase = true)
            val backgroundColor = if (isCorrect) Color(0xFF00C853) else Color(0xFFD50000)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                Text(text = guess, color = Color.White)
            }
        }
    }
}

@Composable
fun MusicGuessScreen(modifier: Modifier = Modifier) {
    val correctSongName = "Ratio" // TODO REMOVE
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.testsong) }
    var isPlaying by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (isPlaying) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.start()
                }
                isPlaying = !isPlaying
            },
            modifier = Modifier
                .size(64.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow, // TODO: change icon to pause icon
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(correctGuessName=correctSongName)
    }
}

@Composable
fun LyricsGuessScreen(modifier: Modifier = Modifier) {
    val correctSongName = "Doucement" // TODO REMOVE
    val lyricsSnippet = "Je te donne ce que tu attends de moi. Et le temps peut s'écouler" // TODO REMOVE

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Spacer(modifier = Modifier.height(16.dp))
        LyricsDisplay(lyricsSnippet = lyricsSnippet)
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(correctGuessName=correctSongName)
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        // TODO: home screen content here
    }
}

@Composable
fun AlbumGuessScreen(modifier: Modifier = Modifier) {
    val correctAlbumName = "Quand la musique est bonne" // TODO REMOVE
    var blurAmount by remember { mutableFloatStateOf(25f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.jjgalbumcover),
            contentDescription = "Album Cover",
            modifier = Modifier
                .size(240.dp)
                .blur(radiusX = blurAmount.dp, radiusY = blurAmount.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit
        )

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

@Composable
fun ArtistGuessScreen(modifier: Modifier = Modifier) {
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
        // TODO artist content here
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(correctGuessName=correctArtistName)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SpotidleTheme {
        MainScreen()
    }
}
