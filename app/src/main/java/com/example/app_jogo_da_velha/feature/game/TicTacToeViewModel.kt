package com.example.app_jogo_da_velha.feature.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TicTacToeViewModel : ViewModel() {
    private val _state = MutableStateFlow(TicTacToeState())

    val state: StateFlow<TicTacToeState> = _state.asStateFlow()
    private val winningCombinations = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Linhas
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Colunas
        listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonais
    )

    //O usuário clica em um botão do tabuleiro
    fun onCellClicked(index: Int) {
        val currentState = _state.value

        if (currentState.board[index] != null || currentState.gameStatus != GameStatus.ONGOING) {
            return
        }

        val newBoard = currentState.board.toMutableList()
        newBoard[index] = currentState.currentPlayer

        val winCombo = checkWin(newBoard)

        if (winCombo != null) {
            // ALGUÉM VENCEU
            val isX = currentState.currentPlayer == Player.X

            _state.update {
                it.copy(
                    board = newBoard,
                    gameStatus = if (isX) GameStatus.PLAYER_X_WON else GameStatus.PLAYER_O_WON,
                    winningCells = winCombo, //Salva as células para brilhar na tela
                    scoreX = if (isX) it.scoreX + 1 else it.scoreX, //Atualiza placar
                    scoreO = if (!isX) it.scoreO + 1 else it.scoreO
                )
            }
        } else if (!newBoard.contains(null)) {
            // DEU VELHA
            _state.update {
                it.copy(
                    board = newBoard,
                    gameStatus = GameStatus.DRAW,
                    draws = it.draws + 1 //Atualiza empates
                )
            }
        } else {
            // O JOGO CONTINUA
            _state.update {
                it.copy(
                    board = newBoard,
                    currentPlayer = if (it.currentPlayer == Player.X) Player.O else Player.X
                )
            }
        }
    }

    fun onResetGame() {
        _state.update {
            it.copy(
                board = List(9) { null },
                currentPlayer = Player.X,
                gameStatus = GameStatus.ONGOING,
                winningCells = emptyList()
            )
        }
    }
    private fun checkWin(board: List<Player?>): List<Int>? {
        for (combo in winningCombinations) {
            val (a, b, c) = combo
            if (board[a] != null && board[a] == board[b] && board[a] == board[c]) {
                return combo
            }
        }
        return null
    }
}