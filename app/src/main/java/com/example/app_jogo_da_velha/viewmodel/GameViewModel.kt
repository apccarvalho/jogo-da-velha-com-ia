package com.example.app_jogo_da_velha.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_jogo_da_velha.domain.GameLogic
import com.example.app_jogo_da_velha.domain.ai.AiFactory
import com.example.app_jogo_da_velha.model.Difficulty
import com.example.app_jogo_da_velha.model.GameMode
import com.example.app_jogo_da_velha.model.GameUiState
import com.example.app_jogo_da_velha.model.Player
import com.example.app_jogo_da_velha.model.Score
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// @JvmOverloads gera o construtor (SavedStateHandle) usado pela fábrica padrão do viewModel()
class GameViewModel @JvmOverloads constructor(
    private val savedStateHandle: SavedStateHandle,
    private val aiDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val aiDelayMillis: Long = 500L
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(
            mode = savedStateHandle.get<String>(KEY_MODE)
                ?.let { GameMode.valueOf(it) } ?: GameMode.PVP,
            difficulty = savedStateHandle.get<String>(KEY_DIFFICULTY)
                ?.let { Difficulty.valueOf(it) } ?: Difficulty.HARD,
            scorePvp = restoreScore(GameMode.PVP),
            scorePve = restoreScore(GameMode.PVE)
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Jogada da IA em andamento
    private var aiJob: Job? = null

    // Muda a cada resetGame(); uma jogada da IA só é aplicada na partida em que foi pedida
    private var gameId = 0

    fun onCellClick(index: Int) {
        val state = _uiState.value
        if (!state.boardEnabled || state.board[index] != null) return

        applyMove(index, state.currentPlayer)

        val next = _uiState.value
        if (next.mode == GameMode.PVE && !next.isGameOver && next.currentPlayer == AI_PLAYER) {
            startAiTurn()
        }
    }

    fun resetGame() {
        cancelAi()
        gameId++
        _uiState.update {
            it.copy(
                board = List(9) { null },
                currentPlayer = Player.X,
                winner = null,
                winningLine = null,
                isDraw = false,
                isAiThinking = false
            )
        }
    }

    /** Zera só o placar do modo atual. */
    fun resetScore() {
        updateScore(_uiState.value.mode, Score())
    }

    /** Troca o modo e reinicia o tabuleiro; os dois placares são mantidos. */
    fun setMode(mode: GameMode) {
        cancelAi()
        _uiState.update { it.copy(mode = mode) }
        savedStateHandle[KEY_MODE] = mode.name
        resetGame()
    }

    /** Troca a dificuldade e reinicia o tabuleiro; o placar é mantido. */
    fun setDifficulty(difficulty: Difficulty) {
        cancelAi()
        _uiState.update { it.copy(difficulty = difficulty) }
        savedStateHandle[KEY_DIFFICULTY] = difficulty.name
        resetGame()
    }

    private fun startAiTurn() {
        val startedGameId = gameId
        val state = _uiState.value
        val ai = AiFactory.create(state.difficulty)

        // Trava o tabuleiro já na mesma atualização de estado
        _uiState.update { it.copy(isAiThinking = true) }

        aiJob = viewModelScope.launch {
            delay(aiDelayMillis)
            // O Minimax pode ser pesado: calcula fora da thread principal
            val move = withContext(aiDispatcher) { ai.chooseMove(state.board, AI_PLAYER) }

            // Se a partida foi reiniciada no meio do caminho, descarta a jogada
            ensureActive()
            if (startedGameId != gameId) return@launch

            applyMove(move, AI_PLAYER)
            _uiState.update { it.copy(isAiThinking = false) }
        }
    }

    /** Coloca a peça de [player], verifica vitória/empate e atualiza o placar do modo atual. */
    private fun applyMove(index: Int, player: Player) {
        val state = _uiState.value
        val newBoard = GameLogic.applyMove(state.board, index, player)
        val win = GameLogic.checkWinner(newBoard)

        when {
            win != null -> {
                val (winner, line) = win
                val score = state.currentScore.let {
                    if (winner == Player.X) it.copy(xWins = it.xWins + 1)
                    else it.copy(oWins = it.oWins + 1)
                }
                _uiState.update { it.copy(board = newBoard, winner = winner, winningLine = line) }
                updateScore(state.mode, score)
            }

            GameLogic.isDraw(newBoard) -> {
                val score = state.currentScore.let { it.copy(draws = it.draws + 1) }
                _uiState.update { it.copy(board = newBoard, isDraw = true) }
                updateScore(state.mode, score)
            }

            else -> _uiState.update {
                it.copy(board = newBoard, currentPlayer = player.opponent)
            }
        }
    }

    private fun cancelAi() {
        aiJob?.cancel()
        aiJob = null
        _uiState.update { it.copy(isAiThinking = false) }
    }

    private fun updateScore(mode: GameMode, score: Score) {
        _uiState.update {
            if (mode == GameMode.PVE) it.copy(scorePve = score) else it.copy(scorePvp = score)
        }
        val prefix = mode.name
        savedStateHandle["${prefix}_$KEY_X_WINS"] = score.xWins
        savedStateHandle["${prefix}_$KEY_O_WINS"] = score.oWins
        savedStateHandle["${prefix}_$KEY_DRAWS"] = score.draws
    }

    private fun restoreScore(mode: GameMode): Score {
        val prefix = mode.name
        return Score(
            xWins = savedStateHandle["${prefix}_$KEY_X_WINS"] ?: 0,
            oWins = savedStateHandle["${prefix}_$KEY_O_WINS"] ?: 0,
            draws = savedStateHandle["${prefix}_$KEY_DRAWS"] ?: 0
        )
    }

    private companion object {
        // No modo PVE o humano é X e a IA é O
        val AI_PLAYER = Player.O

        const val KEY_MODE = "mode"
        const val KEY_DIFFICULTY = "difficulty"
        const val KEY_X_WINS = "x_wins"
        const val KEY_O_WINS = "o_wins"
        const val KEY_DRAWS = "draws"
    }
}
