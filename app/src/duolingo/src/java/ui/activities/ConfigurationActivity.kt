package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.hideWhenLessonCompletedKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.DuolingoProgressTarget

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            // get target settings
            val hideWhenCompleted = context.dataStore.get(hideWhenLessonCompletedKey) == true

            PluginTheme {
                PreferenceLayout("Duolingo") {
                    PreferenceHeading("Progress target")

                    PreferenceSwitch (
                        icon = R.drawable.eye_off,
                        title = "Hide when completed",
                        description = "Hides target if lesson is completed",
                        onCheckedChange = { value ->
                            context.dataStore.save(hideWhenLessonCompletedKey, value)

                            SmartspacerTargetProvider.notifyChange(context, DuolingoProgressTarget::class.java)
                        },
                        checked = hideWhenCompleted
                    )
                }
            }
        }
    }
}