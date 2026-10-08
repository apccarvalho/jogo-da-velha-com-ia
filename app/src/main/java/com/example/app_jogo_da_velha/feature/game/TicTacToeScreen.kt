package com.example.app_jogo_da_velha.feature.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        ScoreBoard(state = state)

        GameStatusText(status = state.gameStatus, currentPlayer = state.currentPlayer)

        GameGrid(
            board = state.board,
            winningCells = state.winningCells,
            onCellClick = { index -> viewModel.onCellClicked(index) }
        )

        Button(
            onClick = { viewModel.onResetGame() },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(text = "Reiniciar Jogo", fontSize = 18.sp)
        }
    }
}

@Composable
fun ScoreBoard(state: TicTacToeState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ScoreText("X", state.scoreX, Color.Blue)
        ScoreText("Empates", state.draws, Color.Gray)
        ScoreText("O", state.scoreO, Color.Red)
    }
}

@Composable
fun ScoreText(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontWeight = FontWeight.Bold, color = color, fontSize = 20.sp)
        Text(text = score.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun GameStatusText(status: GameStatus, currentPlayer: Player) {
    val text = when (status) {
        GameStatus.ONGOING -> "Vez do Jogador: ${currentPlayer.name}"
        GameStatus.PLAYER_X_WON -> "🏆 JOGADOR X VENCEU! 🏆"
        GameStatus.PLAYER_O_WON -> "🏆 JOGADOR O VENCEU! 🏆"
        GameStatus.DRAW -> "👵 DEU VELHA! 👵"
    }

    val color = when (status) {
        GameStatus.ONGOING -> Color.DarkGray
        GameStatus.PLAYER_X_WON -> Color.Blue
        GameStatus.PLAYER_O_WON -> Color.Red
        GameStatus.DRAW -> Color.Gray
    }

    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun GameGrid(
    board: List<Player?>,
    winningCells: List<Int>,
    onCellClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(16.dp)
            .aspectRatio(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(board) { index, player ->

            val isWinningCell = winningCells.contains(index)

            GameCell(
                player = player,
                isWinningCell = isWinningCell,
                onClick = { onCellClick(index) }
            )
        }
    }
}

@Composable
fun GameCell(
    player: Player?,
    isWinningCell: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor = if (isWinningCell) Color.Green.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.3f)

    val textColor = when (player) {
        Player.X -> Color.Blue
        Player.O -> Color.Red
        null -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = player?.name ?: "",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = textColor
        )
    }
}