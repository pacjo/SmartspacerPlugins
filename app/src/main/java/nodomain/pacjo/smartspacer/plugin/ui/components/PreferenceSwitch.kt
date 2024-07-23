package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mikepenz.iconics.typeface.IIcon

/**
 * Represents a two-state (Boolean) toggleable preference.
 *
 * @param icon The icon displayed to the left side of the preference.
 * @param title The title of the preference.
 * @param description Additional information regarding the preference.
 * @param onCheckedChange Callback when preference state is changed.
 * @param checked Whether the preference is checked or not.
 */
@Composable
fun PreferenceSwitch(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean = false
) {
    var isChecked by remember { mutableStateOf(checked) }

    Preference(
        icon = icon,
        title = title,
        modifier = Modifier.clickable {
            // Change state, then callback
            isChecked = !isChecked
            onCheckedChange(isChecked)
        },
        description = {
            Text(text = description)
        },
        endWidget = {
            Switch(
                checked = isChecked,
                onCheckedChange = { state ->
                    isChecked = state
                    onCheckedChange(isChecked)
                },
            )
        },
    )

}

@Composable
fun PreferenceSwitch(
    icon: IIcon,
    title: String,
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean = false
) {
    var isChecked by remember { mutableStateOf(checked) }

    Preference(
        icon = icon,
        title = title,
        modifier = Modifier.clickable {
            // Change state, then callback
            isChecked = !isChecked
            onCheckedChange(isChecked)
        },
        description = {
            Text(text = description)
        },
        endWidget = {
            Switch(
                checked = isChecked,
                onCheckedChange = { state ->
                    isChecked = state
                    onCheckedChange(isChecked)
                },
            )
        },
    )

}