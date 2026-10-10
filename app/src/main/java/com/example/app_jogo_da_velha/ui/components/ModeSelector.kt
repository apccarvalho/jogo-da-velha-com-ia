package com.example.app_jogo_da_velha.ui.components

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.app_jogo_da_velha.R
import com.example.app_jogo_da_velha.model.GameMode

private val modeOptions = listOf(
    GameMode.PVE to R.string.mode_pve,
    GameMode.PVP to R.string.mode_pvp
)

@Composable
fun ModeSelector(
    mode: GameMode,
    onModeChange: (GameMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        modeOptions.forEachIndexed { index, (option, label) ->
            SegmentedButton(
                selected = option == mode,
                onClick = { onModeChange(option) },
                enabled = enabled,
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modeOptions.size)
            ) {
                Text(text = stringResource(label))
            }
        }
    }
}
