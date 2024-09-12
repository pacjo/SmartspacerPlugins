package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.GenericSunTimesComplication
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

class SunTimesComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")

            val sunTimesComplicationTrimToFit = preferences.optBoolean("suntimes_complication_trim_to_fit", true)

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Sun times complication")

                    PreferenceSwitch(
                        icon = R.drawable.content_cut,
                        title = "Complication text trimming",
                        description = "Disable this if text is getting cut off. May cause unexpected results",
                        onCheckedChange = {
                            value -> savePreference(context,"suntimes_complication_trim_to_fit", value)

                            SmartspacerComplicationProvider.notifyChange(context, GenericSunTimesComplication::class.java)
                        },
                        checked = sunTimesComplicationTrimToFit
                    )
                }
            }
        }
    }
}