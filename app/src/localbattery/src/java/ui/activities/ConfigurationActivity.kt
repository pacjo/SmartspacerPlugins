package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.ChargingStatusComplication
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.savePreference
import org.json.JSONObject
import targets.LocalBatteryTarget
import java.io.File

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            // get number of forecast points (as we need it to show the default)
            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")
            val showEstimate = preferences.optBoolean("target_show_estimate", true)
            val disableComplicationTextTrimming = preferences.optBoolean("complication_disable_trimming", false)

            PluginTheme {
                PreferenceLayout("Local Battery") {
                    PreferenceHeading("Charging target")

                    PreferenceSwitch (
                        icon = R.drawable.clock_time_ten_outline,
                        title = "Charging estimate",
                        description = "Show time to charge",
                        onCheckedChange = {
                            value -> savePreference(context,"target_show_estimate", value)
                            SmartspacerTargetProvider.notifyChange(context, LocalBatteryTarget::class.java)
                        },
                        checked = showEstimate
                    )

                    PreferenceSwitch (
                        icon = R.drawable.content_cut,
                        title = "Disable Complication Text Trimming",
                        description = "Allows longer text in charging complication",
                        onCheckedChange = {
                            value -> savePreference(context,"complication_disable_trimming", value)
                            SmartspacerComplicationProvider.notifyChange(context, ChargingStatusComplication::class.java)
                        },
                        checked = disableComplicationTextTrimming
                    )
                }
            }
        }
    }
}