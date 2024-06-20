package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Allows the user to select from a range of integer values.
 *
 * @param icon The icon displayed to the left side of the preference.
 * @param title The title of the preference.
 * @param description Additional information regarding the preference.
 * @param onSliderChange Callback when preference state is changed.
 * @param range The minimum and maximum range of the slider.
 * @param defaultPosition The default position of the slider.
 */
@Composable
fun PreferenceSlider(
    icon: Int,
    title: String,
    description: String,
    onSliderChange: (value: Int) -> Unit,
    range: IntRange,
    defaultPosition: Float = 4f
) {
    var sliderPosition by remember { mutableFloatStateOf(defaultPosition) }

    Preference(
        icon = icon,
        title = title,
        description = {
            Text(text = description)
            Spacer(modifier = Modifier.requiredHeight(16.dp))
            Slider(
                value = sliderPosition,
                onValueChange = {
                    // Round to the nearest integer value between 0 and 4
                    val value: Int =
                        it.coerceIn(range.first.toFloat()..range.last.toFloat()).roundToInt()
                    sliderPosition = value.toFloat()

                    onSliderChange(value)
                },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                steps = range.last - range.first - 1,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
    )
}
