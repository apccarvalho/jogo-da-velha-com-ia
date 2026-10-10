package com.example.app_jogo_da_velha.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.app_jogo_da_velha.MainDispatcherRule
import com.example.app_jogo_da_velha.model.Difficulty
import com.example.app_jogo_da_velha.model.GameMode
import com.example.app_jogo_da_velha.model.Player
import com.example.app_jogo_da_velha.model.Score
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle = SavedStateHandle()
    private val viewModel = createViewModel()

    private val state get() = viewModel.uiState.value

    private fun createViewModel() = GameViewModel(
        savedStateHandle = savedStateHandle,
        aiDispatcher = mainDispatcherRule.testDispatcher,
        aiDelayMillis = AI_DELAY
    )

    /** PVP: X joga 0, 1, 2 e O joga 3, 4 → vitória do X na linha superior. */
    private fun playXWinsPvp() {
        listOf(0, 3, 1, 4, 2).forEach(viewModel::onCellClick)
    }

    /**
     * PVE no difícil: o humano sempre joga na primeira célula livre.
     * X:0 → IA:4, X:1 → IA:2 (bloqueio), X:3 → IA:6 (vence na diagonal 2-4-6).
     */
    private fun TestScope.playAiWinsPve() {
        while (!state.isGameOver) {
            viewModel.onCellClick(state.board.indexOfFirst { it == null })
            advanceUntilIdle()
        }
    }

    @Test
    fun `trocar de modo reinicia o tabuleiro e preserva os placares`() = runTest {
        playXWinsPvp()
        viewModel.setMode(GameMode.PVE)
        playAiWinsPve()

        viewModel.setMode(GameMode.PVP)

        assertEquals(GameMode.PVP, state.mode)
        assertTrue(state.board.all { it == null })
        assertEquals(Player.X, state.currentPlayer)
        assertNull(state.winner)
        assertNull(state.winningLine)
        assertFalse(state.isGameOver)
        assertEquals(Score(xWins = 1), state.scorePvp)
        assertEquals(Score(oWins = 1), state.scorePve)
    }

    @Test
    fun `vitoria no PVP incrementa so scorePvp`() {
        playXWinsPvp()

        assertEquals(Player.X, state.winner)
        assertEquals(listOf(0, 1, 2), state.winningLine)
        assertEquals(Score(xWins = 1), state.scorePvp)
        assertEquals(Score(), state.scorePve)
    }

    @Test
    fun `vitoria da IA no PVE incrementa so scorePve`() = runTest {
        viewModel.setMode(GameMode.PVE)
        playAiWinsPve()

        assertEquals(Player.O, state.winner)
        assertEquals(listOf(2, 4, 6), state.winningLine)
        assertEquals(Score(oWins = 1), state.scorePve)
        assertEquals(Score(), state.scorePvp)
    }

    @Test
    fun `resetScore zera so o placar do modo atual`() = runTest {
        playXWinsPvp()
        viewModel.setMode(GameMode.PVE)
        playAiWinsPve()

        viewModel.resetScore()

        assertEquals(Score(), state.scorePve)
        assertEquals(Score(xWins = 1), state.scorePvp)
    }

    @Test
    fun `jogada apos o fim da partida e ignorada`() {
        playXWinsPvp()
        viewModel.onCellClick(8)

        assertNull(state.board[8])
        assertEquals(Score(xWins = 1), state.scorePvp)
    }

    @Test
    fun `estado salvo e restaurado apos recriar o ViewModel`() = runTest {
        playXWinsPvp()
        viewModel.setMode(GameMode.PVE)
        viewModel.setDifficulty(Difficulty.HARD)
        playAiWinsPve()

        val restored = createViewModel().uiState.value

        assertEquals(GameMode.PVE, restored.mode)
        assertEquals(Difficulty.HARD, restored.difficulty)
        assertEquals(Score(xWins = 1), restored.scorePvp)
        assertEquals(Score(oWins = 1), restored.scorePve)
    }

    // --- Turno da IA ---

    @Test
    fun `no PVE a IA joga sozinha depois do humano`() = runTest {
        viewModel.setMode(GameMode.PVE)

        viewModel.onCellClick(0)

        // Enquanto a IA pensa, o tabuleiro fica travado
        assertTrue(state.isAiThinking)
        assertFalse(state.boardEnabled)
        viewModel.onCellClick(1)
        assertNull(state.board[1])

        advanceUntilIdle()

        assertFalse(state.isAiThinking)
        assertEquals(Player.O, state.board[4]) // HardAi responde no centro
        assertEquals(Player.X, state.currentPlayer)
        assertEquals(2, state.board.count { it != null })
    }

    @Test
    fun `resetGame durante isAiThinking cancela a jogada da IA`() = runTest {
        viewModel.setMode(GameMode.PVE)
        viewModel.onCellClick(0)
        advanceTimeBy(AI_DELAY / 2)
        assertTrue(state.isAiThinking)

        viewModel.resetGame()
        advanceUntilIdle()

        assertFalse(state.isAiThinking)
        assertTrue(state.board.all { it == null })
        assertEquals(Player.X, state.currentPlayer)
    }

    @Test
    fun `setDifficulty durante isAiThinking cancela a jogada da IA`() = runTest {
        viewModel.setMode(GameMode.PVE)
        viewModel.onCellClick(0)
        advanceTimeBy(AI_DELAY / 2)

        viewModel.setDifficulty(Difficulty.EASY)
        advanceUntilIdle()

        assertFalse(state.isAiThinking)
        assertTrue(state.board.all { it == null })
        assertEquals(Difficulty.EASY, state.difficulty)
    }

    @Test
    fun `no PVP o O continua sendo jogado manualmente`() = runTest {
        viewModel.onCellClick(0)
        advanceUntilIdle()

        assertFalse(state.isAiThinking)
        assertEquals(Player.O, state.currentPlayer)
        assertEquals(1, state.board.count { it != null })
    }

    private companion object {
        const val AI_DELAY = 500L
    }
}
