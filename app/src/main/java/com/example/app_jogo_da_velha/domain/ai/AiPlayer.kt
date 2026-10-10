package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.model.Player

interface AiPlayer {
    /** Retorna o índice (0 a 8) da célula escolhida pela IA. */
    fun chooseMove(board: List<Player?>, aiPlayer: Player): Int
}
