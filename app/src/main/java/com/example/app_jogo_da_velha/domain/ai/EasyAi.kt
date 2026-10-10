package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.domain.GameLogic
import com.example.app_jogo_da_velha.model.Player
import kotlin.random.Random

/** Fácil: joga em uma célula vazia qualquer. */
class EasyAi(
    private val random: Random = Random.Default
) : AiPlayer {

    override fun chooseMove(board: List<Player?>, aiPlayer: Player): Int =
        GameLogic.availableMoves(board).random(random)
}
