package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore
import targets.LivelyGreetingTarget
import targets.dataStore

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val hideNoComplications = getBoolFromDataStore(context.dataStore, "target_hide_no_complications") ?: false

            PluginTheme {
                PreferenceLayout("Lively Greeting") {
                    PreferenceHeading("Greeting target")

                    PreferenceSwitch(
                        icon = CommunityMaterial.Icon.cmd_eye_off,
                        title = "Dynamically hide",
                        description = "Hide target when no complications are available",
                        onCheckedChange = {
                            value -> saveToDataStore(context.dataStore, "target_hide_no_complications", value)
                            SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
                        },
                        checked = hideNoComplications
                    )
                }
            }
        }
    }
}
