package com.example.app_jogo_da_velha.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_jogo_da_velha.R
import com.example.app_jogo_da_velha.model.GameMode
import com.example.app_jogo_da_velha.model.Player

@Composable
fun StatusPanel(
    mode: GameMode,
    currentPlayer: Player,
    winner: Player?,
    isDraw: Boolean,
    isAiThinking: Boolean,
    modifier: Modifier = Modifier
) {
    val isPve = mode == GameMode.PVE

    val text = when {
        winner != null && isPve ->
            stringResource(if (winner == Player.X) R.string.status_you_won else R.string.status_ai_won)
        winner != null -> stringResource(R.string.status_winner, winner.name)
        isDraw -> stringResource(R.string.status_draw)
        isPve && isAiThinking -> stringResource(R.string.status_ai_thinking)
        isPve -> stringResource(R.string.status_your_turn)
        else -> stringResource(R.string.status_turn, currentPlayer.name)
    }

    val color = when {
        winner == Player.X -> Color.Blue
        winner == Player.O -> Color.Red
        isDraw -> Color.Gray
        else -> Color.DarkGray
    }

    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isPve && isAiThinking && winner == null && !isDraw) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        }
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
