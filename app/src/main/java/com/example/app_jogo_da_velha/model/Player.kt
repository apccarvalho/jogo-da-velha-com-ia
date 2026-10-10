package com.example.app_jogo_da_velha.model

enum class Player {
    X, O;

    val opponent: Player
        get() = if (this == X) O else X
}
