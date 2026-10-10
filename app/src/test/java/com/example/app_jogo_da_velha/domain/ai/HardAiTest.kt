package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.random.Random

class HardAiTest {

    private val ai = HardAi()

    @Test
    fun `tabuleiro vazio joga no centro`() {
        assertEquals(4, ai.chooseMove(board("... ... ..."), Player.X))
    }

    @Test
    fun `vence quando pode`() {
        // X X .
        // O O .
        // . . X
        val board = board("XX. OO. ..X")
        assertEquals(5, ai.chooseMove(board, Player.O))
    }

    @Test
    fun `bloqueia quando precisa`() {
        // X X .
        // . O .
        // . . .
        val board = board("XX. .O. ...")
        assertEquals(2, ai.chooseMove(board, Player.O))
    }

    @Test
    fun `nunca joga em celula ocupada`() {
        val random = Random(42)
        repeat(100) {
            val (board, player) = randomOngoingBoard(random)
            val move = ai.chooseMove(board, player)
            assertNull("Jogou na célula ocupada $move em $board", board[move])
        }
    }

    @Test
    fun `nunca perde em 200 partidas contra EasyAi`() {
        val easy = EasyAi(Random(1234))
        repeat(200) { game ->
            val starter = if (game % 2 == 0) Player.X else Player.O
            val winner = playGame(mapOf(Player.X to ai, Player.O to easy), starter)
            assertNotEquals("HardAi perdeu a partida $game", Player.O, winner)
        }
    }

    @Test
    fun `HardAi contra HardAi termina em empate`() {
        val winner = playGame(mapOf(Player.X to HardAi(), Player.O to HardAi()), Player.X)
        assertNull(winner)
    }
}
