package com.example.app_jogo_da_velha.domain

import com.example.app_jogo_da_velha.model.Player

/** Regras do jogo da velha. Funções puras: sem estado e sem dependência de Android. */
object GameLogic {

    val WINNING_LINES: List<List<Int>> = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Linhas
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Colunas
        listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonais
    )

    /** Retorna o vencedor e a linha vencedora, ou null se ninguém venceu. */
    fun checkWinner(board: List<Player?>): Pair<Player, List<Int>>? {
        for (line in WINNING_LINES) {
            val (a, b, c) = line
            val player = board[a]
            if (player != null && player == board[b] && player == board[c]) {
                return player to line
            }
        }
        return null
    }

    /** Empate: tabuleiro cheio e sem vencedor. */
    fun isDraw(board: List<Player?>): Boolean =
        board.none { it == null } && checkWinner(board) == null

    fun availableMoves(board: List<Player?>): List<Int> =
        board.indices.filter { board[it] == null }

    fun applyMove(board: List<Player?>, index: Int, player: Player): List<Player?> {
        require(index in board.indices) { "Índice fora do tabuleiro: $index" }
        require(board[index] == null) { "Célula $index já está ocupada" }
        return board.toMutableList().also { it[index] = player }
    }
}
