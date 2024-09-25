package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import nodomain.pacjo.smartspacer.plugin.R

/**
 * Allows the user to input custom text.
 *
 * @param icon The icon displayed to the left side of the preference.
 * @param title The title of the preference.
 * @param description Additional information regarding the preference.
 * @param onTextChange Callback when preference state is changed.
 * @param dialogTitle The primary title of the input dialog.
 * @param defaultText The default text used within the text input.
 * @param textFieldTextStyle Text style for popup input field
 */
@Composable
fun PreferenceInput(
    @DrawableRes icon: Int?,
    title: String,
    description: String,
    onTextChange: (value: String) -> Unit,
    dialogTitle: String,
    defaultText: String,
    textFieldTextStyle: TextStyle = LocalTextStyle.current
) {
    val textState = remember { mutableStateOf(defaultText) }
    var isShown by remember { mutableStateOf(false) }

    Preference(
        icon = icon,
        title = title,
        modifier = Modifier.clickable {
            isShown = true
        },
        description = {
            Text(description)
        }
    )

    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                isShown = false
            },
            title = { Text(dialogTitle) },
            text = {
                TextField(
                    value = textState.value,
                    onValueChange = {
                        textState.value = it
                    },
                    textStyle = textFieldTextStyle
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isShown = false
                        onTextChange(textState.value)
                    }
                ) {
                    Text(stringResource(R.string.prompt_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isShown = false
                    }
                ) {
                    Text(stringResource(R.string.prompt_cancel))
                }
            }
        )
    }
}