package com.example.app_jogo_da_velha.domain.ai

import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.random.Random

class EasyAiTest {

    @Test
    fun `sempre retorna uma celula vazia`() {
        val random = Random(42)
        val ai = EasyAi(Random(7))

        repeat(100) {
            val (board, player) = randomOngoingBoard(random)
            val move = ai.chooseMove(board, player)
            assertNull("Jogou na célula ocupada $move em $board", board[move])
        }
    }
}
