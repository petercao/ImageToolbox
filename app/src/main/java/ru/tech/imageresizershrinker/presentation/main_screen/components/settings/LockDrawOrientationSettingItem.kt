package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ScreenLockRotation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun LockDrawOrientationSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = SettingsShapeDefaults.defaultShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        resultModifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        modifier = modifier,
        applyHorPadding = false,
        onClick = onClick,
        title = stringResource(R.string.lock_draw_orientation),
        subtitle = stringResource(R.string.lock_draw_orientation_sub),
        checked = settingsState.lockDrawOrientation,
        startContent = {
            Icon(
                imageVector = Icons.Rounded.ScreenLockRotation,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}