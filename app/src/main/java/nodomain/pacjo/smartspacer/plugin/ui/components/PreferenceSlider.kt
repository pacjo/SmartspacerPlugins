package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

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
    defaultPosition: Int
) {
    var sliderPosition by remember { mutableIntStateOf(defaultPosition) }

    Preference(
        icon = icon,
        title = title,
        description = {
            Text(text = description)

            Spacer(modifier = Modifier.requiredHeight(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = sliderPosition.toFloat(),
                    onValueChange = {
                        val value = it.toInt()

                        sliderPosition = value
                        onSliderChange(value)
                    },
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    steps = range.last - range.first - 1,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Text(
                        text = sliderPosition.toString(),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    )
}