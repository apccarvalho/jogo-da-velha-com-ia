package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.domain.GameLogic
import com.example.app_jogo_da_velha.model.Player
import kotlin.random.Random

/**
 * Médio: heurística simples, em ordem de prioridade:
 * 1) vence se puder; 2) bloqueia a vitória do humano; 3) centro; 4) canto aleatório;
 * 5) qualquer célula livre.
 */
class MediumAi(
    private val random: Random = Random.Default
) : AiPlayer {

    override fun chooseMove(board: List<Player?>, aiPlayer: Player): Int {
        val moves = GameLogic.availableMoves(board)

        winningMove(board, moves, aiPlayer)?.let { return it }
        winningMove(board, moves, aiPlayer.opponent)?.let { return it }
        if (CENTER in moves) return CENTER

        val freeCorners = CORNERS.filter { it in moves }
        if (freeCorners.isNotEmpty()) return freeCorners.random(random)

        return moves.random(random)
    }

    /** Primeira jogada com a qual [player] venceria imediatamente, ou null. */
    private fun winningMove(board: List<Player?>, moves: List<Int>, player: Player): Int? =
        moves.firstOrNull { move ->
            GameLogic.checkWinner(GameLogic.applyMove(board, move, player))?.first == player
        }

    private companion object {
        const val CENTER = 4
        val CORNERS = listOf(0, 2, 6, 8)
    }
}
