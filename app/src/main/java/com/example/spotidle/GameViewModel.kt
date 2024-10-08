import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.spotidle.GameState

class GameViewModel : ViewModel() {
    private var _gameStates by mutableStateOf<Map<String, GameState>>(
        mapOf(
        "lyricsGuess" to GameState.PLAYING,
        "musicGuess" to GameState.PLAYING,
        "albumGuess" to GameState.PLAYING,
        "artistGuess" to GameState.PLAYING
        )
    )

    fun getGameStates(): Map<String, GameState> {
        return _gameStates
    }

    fun getGameState(index: String): GameState {
        return _gameStates.get(index)!!
    }

    fun setGameState(key: String, gameState: GameState) {
        _gameStates = _gameStates.toMutableMap().apply {
            set(key, gameState)
        }
    }
}
