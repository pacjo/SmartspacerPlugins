package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Represents a clickable preference. Meant to trigger some action on user click / touch.
 * @param icon The icon displayed to the left side of the preference.
 * @param title The title of the preference.
 * @param description Additional information regarding the preference.
 * @param onClick Action to execute on user click
 */
@Composable
fun PreferenceButton(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Preference(
        icon = icon,
        title = title,
        description = { Text(description) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}