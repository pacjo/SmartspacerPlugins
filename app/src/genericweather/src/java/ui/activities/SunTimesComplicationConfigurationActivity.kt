package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.SunTimesComplication
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.sunTimesComplicationTrimToFitKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import ui.composables.GeneralSettings

class SunTimesComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val trimToFit = context.dataStore.get(sunTimesComplicationTrimToFitKey) != false

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Sun times complication")

                    PreferenceSwitch(
                        icon = R.drawable.content_cut,
                        title = "Complication text trimming",
                        description = "Disable this if text is getting cut off. May cause unexpected results",
                        onCheckedChange = {
                            value -> context.dataStore.save(sunTimesComplicationTrimToFitKey, value)

                            SmartspacerComplicationProvider.notifyChange(context, SunTimesComplication::class.java)
                        },
                        checked = trimToFit
                    )
                }
            }
        }
    }
}