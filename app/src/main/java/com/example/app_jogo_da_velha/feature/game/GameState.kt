package com.example.app_jogo_da_velha.feature.game
enum class Player {
    X, O
}
enum class GameStatus {
    ONGOING,       // Em andamento
    PLAYER_X_WON,  // Vitória do X
    PLAYER_O_WON,  // Vitória do O
    DRAW           // Empate (Deu Velha)
}

data class TicTacToeState(

    val board: List<Player?> = List(9) { null },

    val currentPlayer: Player = Player.X,

    val gameStatus: GameStatus = GameStatus.ONGOING,

    //Guarda os índices (0 a 8) das 3 células que formaram a vitória
    val winningCells: List<Int> = emptyList(),

    val scoreX: Int = 0,
    val scoreO: Int = 0,
    val draws: Int = 0
)