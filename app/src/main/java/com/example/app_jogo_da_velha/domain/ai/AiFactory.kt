package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.model.Difficulty

object AiFactory {
    fun create(difficulty: Difficulty): AiPlayer = when (difficulty) {
        Difficulty.EASY -> EasyAi()
        Difficulty.MEDIUM -> MediumAi()
        Difficulty.HARD -> HardAi()
    }
}
