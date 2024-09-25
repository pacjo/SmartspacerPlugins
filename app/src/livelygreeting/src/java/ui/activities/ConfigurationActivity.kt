package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.hideTargetWithoutComplicationsKey
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.LivelyGreetingTarget

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val hideNoComplications = context.dataStore.get(hideTargetWithoutComplicationsKey) ?: false

            PluginTheme {
                PreferenceLayout("Lively Greeting") {
                    PreferenceHeading("Greeting target")

                    PreferenceSwitch(
                        iicon = CommunityMaterial.Icon.cmd_eye_off,
                        title = "Dynamically hide",
                        description = "Hide target when no complications are available",
                        onCheckedChange = { value ->
                            context.dataStore.save(hideTargetWithoutComplicationsKey, value)

                            SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
                        },
                        checked = hideNoComplications
                    )
                }
            }
        }
    }
}