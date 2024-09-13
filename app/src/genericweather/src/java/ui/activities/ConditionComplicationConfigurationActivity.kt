package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.WeatherConditionComplication
import data.DataStoreManager.Companion.conditionComplicationStyleKey
import data.DataStoreManager.Companion.conditionComplicationTrimToFitKey
import data.DataStoreManager.Companion.dataStore
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceMenu
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import ui.composables.GeneralSettings

class ConditionComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val trimToFit = context.dataStore.get(conditionComplicationTrimToFitKey) ?: true

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Weather complication")

                    PreferenceMenu(
                        icon = R.drawable.palette_outline,
                        title = "Style",
                        description = "Select complication style",
                        onItemChange = {
                            value -> context.dataStore.save(conditionComplicationStyleKey, value)

                            SmartspacerComplicationProvider.notifyChange(context, WeatherConditionComplication::class.java)
                        },
                        items = listOf(
                            Pair("Temperature only", "temperature"),
                            Pair("Condition only", "condition"),
                            Pair("Temperature and condition", "both")
                        )
                    )

                    PreferenceSwitch(
                        icon = R.drawable.content_cut,
                        title = "Complication text trimming",
                        description = "Disable this if text is getting cut off. May cause unexpected results",
                        onCheckedChange = { value ->
                            context.dataStore.save(conditionComplicationTrimToFitKey, value)

                            SmartspacerComplicationProvider.notifyChange(context, WeatherConditionComplication::class.java)
                        },
                        checked = trimToFit
                    )
                }
            }
        }
    }
}