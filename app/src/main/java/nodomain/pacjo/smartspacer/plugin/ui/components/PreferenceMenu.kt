package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Shows a single-select list of options to the user.
 *
 * @param icon The icon displayed to the left side of the preference.
 * @param title The title of the preference.
 * @param description Additional information regarding the preference.
 * @param onItemChange Callback when preference state is changed.
 * @param items The list of items.
 */
@Composable
fun PreferenceMenu(
    icon: Int,
    title: String,
    description: String,
    onItemChange: (Any) -> Unit,
    items: List<Pair<String, Any>>
) {
    var isExpanded by remember { mutableStateOf(false) }

    Preference(
        icon = icon,
        title = title,
        modifier = Modifier.clickable {
            isExpanded = true
        },
        description = {
            Text(text = description)
        },
        endWidget = {
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.first) },
                        onClick = {
                            onItemChange(item.second)
                            isExpanded = false
                        }
                    )
                }
            }
        },
    )
}
