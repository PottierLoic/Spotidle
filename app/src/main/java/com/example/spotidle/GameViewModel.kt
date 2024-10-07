import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.spotidle.GameState

class GameViewModel : ViewModel() {
    var attempts by mutableIntStateOf(0)
    var gameState by mutableStateOf(GameState.PLAYING)
    var guesses = mutableStateListOf<String>()
}
