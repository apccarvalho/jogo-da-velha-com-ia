package com.example.app_jogo_da_velha.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_jogo_da_velha.R
import com.example.app_jogo_da_velha.model.GameMode
import com.example.app_jogo_da_velha.model.Score

@Composable
fun ScoreBoard(
    mode: GameMode,
    score: Score,
    modifier: Modifier = Modifier
) {
    val (xLabel, oLabel) = when (mode) {
        GameMode.PVE -> R.string.score_you to R.string.score_ai
        GameMode.PVP -> R.string.score_player_x to R.string.score_player_o
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScoreCard(stringResource(xLabel), score.xWins, Color.Blue, Modifier.weight(1f))
        ScoreCard(stringResource(R.string.score_draws), score.draws, Color.Gray, Modifier.weight(1f))
        ScoreCard(stringResource(oLabel), score.oWins, Color.Red, Modifier.weight(1f))
    }
}

@Composable
private fun ScoreCard(label: String, score: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(text = score.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}
