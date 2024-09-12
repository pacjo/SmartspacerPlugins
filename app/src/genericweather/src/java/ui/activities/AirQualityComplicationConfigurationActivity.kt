package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.GenericAirQualityComplication
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.savePreference
import org.json.JSONObject
import ui.composables.GeneralSettings
import java.io.File

class AirQualityComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")

            val airQualityComplicationShowAlways =
                preferences.optBoolean("air_quality_complication_show_always", false)

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Air quality complication")

                    PreferenceSwitch(
                        icon = R.drawable.eye_off,
                        title = "Always show",
                        description = "Enable this to show complication regardless of current AQI value",
                        onCheckedChange = { value ->
                            savePreference(context, "air_quality_complication_show_always", value)

                            SmartspacerComplicationProvider.notifyChange(
                                context,
                                GenericAirQualityComplication::class.java
                            )
                        },
                        checked = airQualityComplicationShowAlways
                    )
                }
            }
        }
    }
}