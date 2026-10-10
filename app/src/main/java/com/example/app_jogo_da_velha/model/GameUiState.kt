package com.example.app_jogo_da_velha.model

data class GameUiState(
    val board: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    // Guarda os índices (0 a 8) das 3 células que formaram a vitória
    val winningLine: List<Int>? = null,
    val isDraw: Boolean = false,
    val isAiThinking: Boolean = false,
    val mode: GameMode = GameMode.PVP,
    val difficulty: Difficulty = Difficulty.HARD,
    val scorePve: Score = Score(),
    val scorePvp: Score = Score()
) {
    val isGameOver: Boolean
        get() = winner != null || isDraw

    val boardEnabled: Boolean
        get() = !isGameOver && !isAiThinking

    val currentScore: Score
        get() = if (mode == GameMode.PVE) scorePve else scorePvp
}
