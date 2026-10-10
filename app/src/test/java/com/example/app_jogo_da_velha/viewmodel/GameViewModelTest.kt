package com.example.app_jogo_da_velha.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.app_jogo_da_velha.model.Difficulty
import com.example.app_jogo_da_velha.model.GameMode
import com.example.app_jogo_da_velha.model.Player
import com.example.app_jogo_da_velha.model.Score
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {

    private val savedStateHandle = SavedStateHandle()
    private val viewModel = GameViewModel(savedStateHandle)

    private val state get() = viewModel.uiState.value

    /** X joga 0, 1, 2 e O joga 3, 4: vitória do X na linha superior. */
    private fun playXWins() {
        listOf(0, 3, 1, 4, 2).forEach(viewModel::onCellClick)
    }

    @Test
    fun `trocar de modo reinicia o tabuleiro e preserva os placares`() {
        playXWins()
        viewModel.setMode(GameMode.PVE)
        viewModel.onCellClick(4)

        viewModel.setMode(GameMode.PVP)

        assertEquals(GameMode.PVP, state.mode)
        assertTrue(state.board.all { it == null })
        assertEquals(Player.X, state.currentPlayer)
        assertNull(state.winner)
        assertNull(state.winningLine)
        assertFalse(state.isGameOver)
        assertEquals(Score(xWins = 1), state.scorePvp)
        assertEquals(Score(), state.scorePve)
    }

    @Test
    fun `vitoria no PVP incrementa so scorePvp`() {
        playXWins()

        assertEquals(Player.X, state.winner)
        assertEquals(listOf(0, 1, 2), state.winningLine)
        assertEquals(Score(xWins = 1), state.scorePvp)
        assertEquals(Score(), state.scorePve)
    }

    @Test
    fun `vitoria no PVE incrementa so scorePve`() {
        viewModel.setMode(GameMode.PVE)
        playXWins()

        assertEquals(Score(xWins = 1), state.scorePve)
        assertEquals(Score(), state.scorePvp)
    }

    @Test
    fun `resetScore zera so o placar do modo atual`() {
        playXWins()
        viewModel.setMode(GameMode.PVE)
        playXWins()

        viewModel.resetScore()

        assertEquals(Score(), state.scorePve)
        assertEquals(Score(xWins = 1), state.scorePvp)
    }

    @Test
    fun `jogada apos o fim da partida e ignorada`() {
        playXWins()
        viewModel.onCellClick(8)

        assertNull(state.board[8])
        assertEquals(Score(xWins = 1), state.scorePvp)
    }

    @Test
    fun `estado salvo e restaurado apos recriar o ViewModel`() {
        playXWins()
        viewModel.setMode(GameMode.PVE)
        viewModel.setDifficulty(Difficulty.EASY)
        playXWins()

        val restored = GameViewModel(savedStateHandle).uiState.value

        assertEquals(GameMode.PVE, restored.mode)
        assertEquals(Difficulty.EASY, restored.difficulty)
        assertEquals(Score(xWins = 1), restored.scorePvp)
        assertEquals(Score(xWins = 1), restored.scorePve)
    }
}
