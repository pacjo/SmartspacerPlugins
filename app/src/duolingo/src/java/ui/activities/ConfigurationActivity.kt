package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.savePreference
import org.json.JSONObject
import targets.DuolingoProgressTarget
import java.io.File

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            // get target settings
            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")
            val hideWhenCompleted = preferences.optBoolean("hide_when_completed", false)

            PluginTheme {
                PreferenceLayout(title = "Duolingo") {
                    PreferenceHeading(
                        heading = "Progress target"
                    )

                    PreferenceSwitch(
                        icon = R.drawable.eye_off,
                        title = "Hide when completed",
                        description = "Hides target if lesson is completed",
                        onCheckedChange = {
                            value -> savePreference(context,"hide_when_completed", value)
                            SmartspacerTargetProvider.notifyChange(context, DuolingoProgressTarget::class.java)
                        },
                        checked = hideWhenCompleted
                    )
                }
            }
        }
    }
}