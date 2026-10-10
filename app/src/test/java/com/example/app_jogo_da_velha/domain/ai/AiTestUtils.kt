package com.example.app_jogo_da_velha.domain.ai

import com.example.app_jogo_da_velha.domain.GameLogic
import com.example.app_jogo_da_velha.model.Player
import kotlin.random.Random

/**
 * Gera um tabuleiro em andamento (sem vencedor e com jogadas livres) com algumas
 * jogadas aleatórias. Retorna o tabuleiro e o jogador da vez.
 */
fun randomOngoingBoard(random: Random): Pair<List<Player?>, Player> {
    while (true) {
        var board: List<Player?> = List(9) { null }
        var player = Player.X
        repeat(random.nextInt(0, 8)) {
            board = GameLogic.applyMove(board, GameLogic.availableMoves(board).random(random), player)
            player = player.opponent
        }
        if (GameLogic.checkWinner(board) == null && GameLogic.availableMoves(board).isNotEmpty()) {
            return board to player
        }
    }
}

/** Joga uma partida completa entre duas IAs e retorna o vencedor (null = empate). */
fun playGame(players: Map<Player, AiPlayer>, starter: Player): Player? {
    var board: List<Player?> = List(9) { null }
    var current = starter
    while (true) {
        val move = players.getValue(current).chooseMove(board, current)
        board = GameLogic.applyMove(board, move, current)
        GameLogic.checkWinner(board)?.let { return it.first }
        if (GameLogic.isDraw(board)) return null
        current = current.opponent
    }
}

/** Monta um tabuleiro a partir de uma string de 9 caracteres: 'X', 'O' ou '.'. */
fun board(cells: String): List<Player?> = cells.filterNot { it.isWhitespace() }.map {
    when (it) {
        'X' -> Player.X
        'O' -> Player.O
        else -> null
    }
}
