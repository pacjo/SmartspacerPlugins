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
import targets.SleepMessagesTarget
import targets.dataStore

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val simpleStyle = getBoolFromDataStore(context.dataStore, "simple_style") ?: false
            val showTimeToBed = getBoolFromDataStore(context.dataStore, "show_time_to_bed") ?: true
            val showAlarmDismissed = getBoolFromDataStore(context.dataStore, "show_alarm_dismissed") ?: true

            PluginTheme {
                PreferenceLayout("Sleep as Android") {
                    PreferenceHeading("Messages target")

                    PreferenceSwitch (
                        icon = R.drawable.cog,
                        title = "Simple style",
                        description = "Show basic target instead of image one",
                        onCheckedChange = {
                            value -> saveToDataStore(context.dataStore,"simple_style", value)
                            SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                        },
                        checked = simpleStyle
                    )

                    PreferenceSwitch (
                        icon = R.drawable.bed_outline,
                        title = "Time to bed",
                        description = "Show time to bed message",
                        onCheckedChange = {
                            value -> saveToDataStore(context.dataStore,"show_time_to_bed", value)
                            SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                        },
                        checked = showTimeToBed
                    )

                    PreferenceSwitch (
                        icon = R.drawable.tea_outline,
                        title = "Alarm dismissed",
                        description = "Show alarm dismissed message",
                        onCheckedChange = {
                            value -> saveToDataStore(context.dataStore,"show_alarm_dismissed", value)
                            SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                        },
                        checked = showAlarmDismissed
                    )
                }
            }
        }
    }
}