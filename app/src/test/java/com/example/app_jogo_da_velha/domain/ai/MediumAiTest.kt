package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.model.Player
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class MediumAiTest {

    private val ai = MediumAi(Random(42))

    @Test
    fun `vence quando tem duas em linha`() {
        // O O .
        // X X .
        // X . .
        val board = board("OO. XX. X..")
        assertEquals(2, ai.chooseMove(board, Player.O))
    }

    @Test
    fun `bloqueia quando o humano tem duas em linha`() {
        // X X .
        // . O .
        // . . .
        val board = board("XX. .O. ...")
        assertEquals(2, ai.chooseMove(board, Player.O))
    }

    @Test
    fun `prefere vencer a bloquear`() {
        // X X .
        // O O .
        // X . .
        val board = board("XX. OO. X..")
        assertEquals(5, ai.chooseMove(board, Player.O))
    }

    @Test
    fun `pega o centro quando livre e sem ameacas`() {
        val board = board("X.. ... ...")
        assertEquals(4, ai.chooseMove(board, Player.O))
    }

    @Test
    fun `pega um canto quando o centro esta ocupado`() {
        val board = board("... .X. ...")
        val move = ai.chooseMove(board, Player.O)
        assert(move in listOf(0, 2, 6, 8)) { "Esperava um canto, veio $move" }
    }
}
