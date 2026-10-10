package com.example.app_jogo_da_velha.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_jogo_da_velha.R
import com.example.app_jogo_da_velha.model.Difficulty
import com.example.app_jogo_da_velha.model.GameMode
import com.example.app_jogo_da_velha.model.GameUiState
import com.example.app_jogo_da_velha.ui.components.BoardGrid
import com.example.app_jogo_da_velha.ui.components.DifficultySelector
import com.example.app_jogo_da_velha.ui.components.ModeSelector
import com.example.app_jogo_da_velha.ui.components.ScoreBoard
import com.example.app_jogo_da_velha.ui.components.StatusPanel
import com.example.app_jogo_da_velha.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenContent(
        state = state,
        onCellClick = viewModel::onCellClick,
        onModeChange = viewModel::setMode,
        onDifficultyChange = viewModel::setDifficulty,
        onResetGame = viewModel::resetGame,
        onResetScore = viewModel::resetScore,
        modifier = modifier
    )
}

@Composable
private fun GameScreenContent(
    state: GameUiState,
    onCellClick: (Int) -> Unit,
    onModeChange: (GameMode) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onResetGame: () -> Unit,
    onResetScore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.game_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        ModeSelector(
            mode = state.mode,
            onModeChange = onModeChange,
            enabled = !state.isAiThinking,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = state.mode == GameMode.PVE) {
            DifficultySelector(
                difficulty = state.difficulty,
                onDifficultyChange = onDifficultyChange,
                enabled = !state.isAiThinking,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ScoreBoard(mode = state.mode, score = state.currentScore)

        StatusPanel(
            mode = state.mode,
            currentPlayer = state.currentPlayer,
            winner = state.winner,
            isDraw = state.isDraw,
            isAiThinking = state.isAiThinking
        )

        BoardGrid(
            board = state.board,
            winningLine = state.winningLine,
            enabled = state.boardEnabled,
            onCellClick = onCellClick
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onResetGame, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.action_reset_game))
            }
            OutlinedButton(onClick = onResetScore, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.action_reset_score))
            }
        }
    }
}
