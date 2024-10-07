package com.example.spotidle.ui.guess

import GameViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spotidle.GameState
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LyricsGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String,
    gameViewModel: GameViewModel = viewModel()
) {

    val context = LocalContext.current
    val correctSongName by remember { mutableStateOf("Doucement") } // TODO CHANGE
    val accessToken = "Bearer I2F2_DAHrB2NqZWmOOAceHfg-HJzNM9F83sJnRbgDdKVEfrxJNcpL764wI86SLu9" // Remplacez par votre access token
    var lyricsSnippet by remember { mutableStateOf("Chargement des paroles...") }

    Log.d("MOI", "LyricsGuessScreen initialisé avec idTrack: $idTrack")

    LaunchedEffect(idTrack) {
        Log.d("MOI", "LaunchedEffect déclenché pour idTrack: $idTrack")
        fetchLyrics(378195, accessToken) { lyrics ->
            lyricsSnippet = lyrics ?: "Erreur lors de la récupération des paroles"
        }
    }

    SpotifightScaffold(navController = navController) {
        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color(0xFF1ED760))
        ) {
            LyricsDisplay(
                lyricsSnippet = lyricsSnippet,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
//        GuessSection(correctGuessName = "Doucement") // TODO : remove
        GuessSection(
            correctGuessName = correctSongName,
            toGuess = "song",
            onGuessSubmit = { guess ->
                if (guess.equals(correctSongName, ignoreCase = true)) {
                    gameViewModel.gameState = GameState.WIN
                } else {
                    gameViewModel.attempts += 1
                    if (gameViewModel.attempts >= 4) {
                        gameViewModel.gameState = GameState.LOOSE
                    }
                }
            },
            viewModel = gameViewModel
        )
    }
}

private suspend fun fetchLyrics(songId: Int, accessToken: String, onResult: (String?) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            Log.d("MOI", "Appel à l'API Genius pour l'ID de la chanson: $songId")
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.genius.com/songs/$songId")
                .header("Authorization", "Bearer I2F2_DAHrB2NqZWmOOAceHfg-HJzNM9F83sJnRbgDdKVEfrxJNcpL764wI86SLu9")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string())
                val lyrics = json.getJSONObject("response")
                    .getJSONObject("song")
                    .getString("lyrics") // Adjust this depending on actual response structure
                Log.d("MOI", "Paroles reçues: $lyrics")
                onResult(lyrics)
            } else {
                Log.e("MOI", "Erreur lors de la requête API Genius: ${response.code}")
                onResult(null)
            }
        } catch (e: Exception) {
            Log.e("MOI", "Erreur lors de la récupération des paroles", e)
            onResult(null) // Handle the error properly
        }
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
