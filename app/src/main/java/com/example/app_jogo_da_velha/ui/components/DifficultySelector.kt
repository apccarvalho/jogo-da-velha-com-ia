package com.example.app_jogo_da_velha.ui.components

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.app_jogo_da_velha.R
import com.example.app_jogo_da_velha.model.Difficulty

private val difficultyOptions = listOf(
    Difficulty.EASY to R.string.difficulty_easy,
    Difficulty.MEDIUM to R.string.difficulty_medium,
    Difficulty.HARD to R.string.difficulty_hard
)

@Composable
fun DifficultySelector(
    difficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        difficultyOptions.forEachIndexed { index, (option, label) ->
            SegmentedButton(
                selected = option == difficulty,
                onClick = { onDifficultyChange(option) },
                enabled = enabled,
                shape = SegmentedButtonDefaults.itemShape(index = index, count = difficultyOptions.size)
            ) {
                Text(text = stringResource(label))
            }
        }
    }
}
