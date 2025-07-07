package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.UvIndexComplication
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.uvIndexComplicationShowAlways
import data.DataStoreManager.Companion.uvIndexComplicationShowThresholdKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSlider
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import ui.composables.GeneralSettings
import utils.UvIndex

class UvIndexComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val showAlways = context.dataStore.get(uvIndexComplicationShowAlways) == true
            val showThreshold = context.dataStore.get(uvIndexComplicationShowThresholdKey) ?: UvIndex.HIGH

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("UV Index complication")

                    PreferenceSlider(
                        icon = R.drawable.counter,
                        title = "Threshold",
                        description = "Index value above which complication should be shown.",
                        onSliderChange = { value ->
                            context.dataStore.save(uvIndexComplicationShowThresholdKey, value)

                            SmartspacerComplicationProvider.notifyChange(
                                context,
                                UvIndexComplication::class.java
                            )
                        },
                        range = UvIndex.LOW..UvIndex.VERY_HIGH,
                        defaultPosition = showThreshold
                    )

                    PreferenceSwitch(
                        icon = R.drawable.eye_off,
                        title = "Always show",
                        description = "Enable this to show complication regardless of current index value",
                        onCheckedChange = { value ->
                            context.dataStore.save(uvIndexComplicationShowAlways, value)

                            SmartspacerComplicationProvider.notifyChange(
                                context,
                                UvIndexComplication::class.java
                            )
                        },
                        checked = showAlways
                    )
                }
            }
        }
    }
}