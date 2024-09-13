package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceMenu
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSlider
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.savePreference
import org.json.JSONObject
import targets.GenericWeatherTarget
import ui.composables.GeneralSettings
import java.io.File

class WeatherTargetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")
            val dataPoints = preferences.optInt("target_points_visible", 4)

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Weather target")

                    PreferenceMenu(
                        icon = R.drawable.palette_outline,
                        title = "Style",
                        description = "Select target style",
                        onItemChange = {
                            value -> savePreference(context,"target_style", value)

                            SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                        },
                        items = listOf(
                            Pair("Temperature only", "temperature"),
                            Pair("Condition only", "condition"),
                            Pair("Temperature and condition", "both")
                        )
                    )

                    PreferenceSlider(
                        icon = R.drawable.calendar_range_outline,
                        title = "Forecast points to show",
                        description = "Select number of visible forecast days/hours",
                        onSliderChange = {
                            value -> savePreference(context,"target_points_visible", value)

                            SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                        },
                        range = (0..4),
                        defaultPosition = dataPoints
                    )
                }
            }
        }
    }
}