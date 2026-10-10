package com.example.app_jogo_da_velha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_jogo_da_velha.model.Player

@Composable
fun Cell(
    player: Player?,
    isWinningCell: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isWinningCell) Color.Green.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.3f)

    val textColor = when (player) {
        Player.X -> Color.Blue
        Player.O -> Color.Red
        null -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled && player == null, onClick = onClick),
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
