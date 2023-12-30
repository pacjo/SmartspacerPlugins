package nodomain.pacjo.smartspacer.plugin.utils

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import java.io.File
import kotlin.math.roundToInt

fun savePreference(context: Context, id: String, value: Any) {
    val file = File(context.filesDir, "data.json")

    // create empty JSONObject if file doesn't exist
    val jsonObject = JSONObject(when (file.exists()) {
        true -> file.readText()
        else -> ""
    })

    val preferencesObject: JSONObject
    if (jsonObject.has("preferences")) {
        preferencesObject = jsonObject.getJSONObject("preferences")
    } else {
        preferencesObject = JSONObject()
        jsonObject.put("preferences", preferencesObject)
    }

    preferencesObject.put(id, value)

    file.writeText(jsonObject.toString())
}

// Boilerplate for other Preference composables
@Composable
fun PreferenceContainer(icon: Int, title: String, subtitle: String) {
    Row {
//        Box {
//            Icon.createWithResource(        // TODO: should work, doesn't
//                LocalContext.current,
//                icon
//            )
//        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun PreferenceText(title: String, subtitle: String) {
    Column {        // this feels wrong
        Column(
            modifier = Modifier
                .weight(4f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun WorsePreferenceSwitch(icon: Int, title: String, subtitle: String, stateCallback: (value: Boolean) -> Unit, checked: Boolean = false) {
    var isChecked by remember { mutableStateOf(checked) }

    Surface(
        onClick = {
            // Change state, then callback
            isChecked = !isChecked
            stateCallback(isChecked)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set the weight of the PreferenceContainer Column

            // TODO:  try to convert the column below to separate composable
//            PreferenceText(title = title, subtitle = subtitle)


            Column(
                modifier = Modifier
                    .weight(4f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Spacer to push Switch to the far right

            Switch(
                checked = isChecked,
                onCheckedChange = { state ->
                    isChecked = state
                    stateCallback(isChecked)
                },
            )
        }
    }
}

@Composable
fun PreferenceSwitch(icon: Int, title: String, subtitle: String, stateCallback: (value: Boolean) -> Unit, checked: Boolean = false) {
    var isChecked by remember { mutableStateOf(checked) }

    Surface(
        onClick = {
            // Change state, then callback
            isChecked = !isChecked
            stateCallback(isChecked)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreferenceContainer(icon, title, subtitle)

            Switch(
                checked = isChecked,
                onCheckedChange = { state ->
                    isChecked = state
                    stateCallback(isChecked)
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun PreferenceMenu(icon: Int, title: String, subtitle: String, stateCallback: (value: Any) -> Unit, items: List<Pair<String, Any>>) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        PreferenceContainer(icon, title, subtitle)

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.first) },
                    onClick = {
                        stateCallback(item.second)

                        // ... and close menu
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PreferenceInput(icon: Int, title: String, subtitle: String, stateCallback: (value: String) -> Unit, dialogText: String, defaultText: String) {
    val textState = remember { mutableStateOf(defaultText) }
    var isShown by remember { mutableStateOf(false) }

    Surface(
        onClick = {
            isShown = true
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreferenceContainer(icon, title, subtitle)
        }
    }

    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                isShown = false
            },
            title = { Text(dialogText) },
            text = {
                TextField(
                    value = textState.value,
                    onValueChange = {
                        textState.value = it
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isShown = false
                        stateCallback(textState.value)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isShown = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun PreferenceSlider(icon: Int, title: String, subtitle: String, stateCallback: (value: Int) -> Unit, range: IntRange, defaultPosition: Float = 4f) {
    var sliderPosition by remember { mutableFloatStateOf(defaultPosition) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        PreferenceContainer(icon, title, subtitle)

        Slider(             // TODO: add label
            value = sliderPosition,
            onValueChange = {
                // Round to the nearest integer value between 0 and 4
                val value: Int = it.coerceIn(range.first.toFloat()..range.last.toFloat()).roundToInt()
                sliderPosition = value.toFloat()

                stateCallback(value)
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(context: Activity, title: String) {
    LargeTopAppBar(
        title = {
            Text(
                title,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                // https://stackoverflow.com/questions/67401294/jetpack-compose-close-application-by-button
                context.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back arrow"
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    )
}