package com.example.app_jogo_da_velha.domain

import com.example.app_jogo_da_velha.model.Player
import com.example.app_jogo_da_velha.model.Player.O
import com.example.app_jogo_da_velha.model.Player.X
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    private val emptyBoard: List<Player?> = List(9) { null }

    private fun boardWith(player: Player, cells: List<Int>): List<Player?> =
        emptyBoard.mapIndexed { index, cell -> if (index in cells) player else cell }

    // --- checkWinner: as 8 linhas de vitória ---

    @Test fun `linha superior vence`() = assertWin(listOf(0, 1, 2))
    @Test fun `linha do meio vence`() = assertWin(listOf(3, 4, 5))
    @Test fun `linha inferior vence`() = assertWin(listOf(6, 7, 8))
    @Test fun `coluna esquerda vence`() = assertWin(listOf(0, 3, 6))
    @Test fun `coluna do meio vence`() = assertWin(listOf(1, 4, 7))
    @Test fun `coluna direita vence`() = assertWin(listOf(2, 5, 8))
    @Test fun `diagonal principal vence`() = assertWin(listOf(0, 4, 8))
    @Test fun `diagonal secundaria vence`() = assertWin(listOf(2, 4, 6))

    private fun assertWin(line: List<Int>) {
        for (player in Player.entries) {
            val result = GameLogic.checkWinner(boardWith(player, line))
            assertEquals(player to line, result)
        }
    }

    @Test
    fun `existem exatamente 8 linhas de vitoria`() {
        assertEquals(8, GameLogic.WINNING_LINES.size)
    }

    @Test
    fun `vitoria com tabuleiro cheio nao e empate`() {
        // X X X
        // O O X
        // X O O
        val board = listOf(X, X, X, O, O, X, X, O, O)
        assertEquals(X to listOf(0, 1, 2), GameLogic.checkWinner(board))
        assertFalse(GameLogic.isDraw(board))
    }

    // --- Empate ---

    @Test
    fun `tabuleiro cheio sem vencedor e empate`() {
        // X O X
        // X O O
        // O X X
        val board = listOf(X, O, X, X, O, O, O, X, X)
        assertNull(GameLogic.checkWinner(board))
        assertTrue(GameLogic.isDraw(board))
        assertTrue(GameLogic.availableMoves(board).isEmpty())
    }

    // --- Em andamento ---

    @Test
    fun `tabuleiro vazio esta em andamento`() {
        assertNull(GameLogic.checkWinner(emptyBoard))
        assertFalse(GameLogic.isDraw(emptyBoard))
    }

    @Test
    fun `tabuleiro parcial sem vencedor esta em andamento`() {
        // X O _
        // _ X _
        // _ _ O
        val board = listOf(X, O, null, null, X, null, null, null, O)
        assertNull(GameLogic.checkWinner(board))
        assertFalse(GameLogic.isDraw(board))
    }

    // --- availableMoves ---

    @Test
    fun `availableMoves no tabuleiro vazio retorna todas as celulas`() {
        assertEquals((0..8).toList(), GameLogic.availableMoves(emptyBoard))
    }

    @Test
    fun `availableMoves retorna apenas celulas livres em ordem`() {
        val board = listOf(X, O, null, null, X, null, null, null, O)
        assertEquals(listOf(2, 3, 5, 6, 7), GameLogic.availableMoves(board))
    }

    // --- applyMove ---

    @Test
    fun `applyMove coloca a peca sem alterar o tabuleiro original`() {
        val result = GameLogic.applyMove(emptyBoard, 4, X)
        assertEquals(X, result[4])
        assertEquals(8, result.count { it == null })
        assertTrue(emptyBoard.all { it == null })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `applyMove em celula ocupada lanca excecao`() {
        GameLogic.applyMove(boardWith(X, listOf(0)), 0, O)
    }
}
