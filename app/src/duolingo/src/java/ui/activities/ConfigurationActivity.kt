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
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore
import targets.DuolingoProgressTarget
import targets.dataStore

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            // get target settings
            val hideWhenCompleted = getBoolFromDataStore(context.dataStore, "hide_when_completed") ?: false

            PluginTheme {
                PreferenceLayout("Duolingo") {
                    PreferenceHeading("Progress target")

                    PreferenceSwitch (
                        icon = R.drawable.eye_off,
                        title = "Hide when completed",
                        description = "Hides target if lesson is completed",
                        onCheckedChange = {
                            value -> saveToDataStore(context.dataStore,"hide_when_completed", value)
                            SmartspacerTargetProvider.notifyChange(context, DuolingoProgressTarget::class.java)
                        },
                        checked = hideWhenCompleted
                    )
                }
            }
        }
    }
}