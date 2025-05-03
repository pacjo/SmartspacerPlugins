package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.AirQualityComplication
import data.DataStoreManager.Companion.airQualityComplicationShowAlways
import data.DataStoreManager.Companion.airQualityComplicationShowThresholdKey
import data.DataStoreManager.Companion.dataStore
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSlider
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import ui.composables.GeneralSettings
import utils.AirQualityThresholds

class AirQualityComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val showAlways = context.dataStore.get(airQualityComplicationShowAlways) == true
            val showThreshold = context.dataStore.get(airQualityComplicationShowThresholdKey) ?: AirQualityThresholds.FAIR

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Air quality complication")

                    PreferenceSlider(
                        icon = R.drawable.counter,
                        title = "Threshold",
                        description = "AQI value above which complication should be shown.",
                        onSliderChange = { value ->
                            context.dataStore.save(airQualityComplicationShowThresholdKey, value)

                            SmartspacerComplicationProvider.notifyChange(
                                context,
                                AirQualityComplication::class.java
                            )
                        },
                        range = AirQualityThresholds.EXCELLENT..AirQualityThresholds.VERY_UNHEALTHY,
                        defaultPosition = showThreshold
                    )

                    PreferenceSwitch(
                        icon = R.drawable.eye_off,
                        title = "Always show",
                        description = "Enable this to show complication regardless of current AQI value",
                        onCheckedChange = { value ->
                            context.dataStore.save(airQualityComplicationShowAlways, value)

                            SmartspacerComplicationProvider.notifyChange(
                                context,
                                AirQualityComplication::class.java
                            )
                        },
                        checked = showAlways
                    )
                }
            }
        }
    }
}