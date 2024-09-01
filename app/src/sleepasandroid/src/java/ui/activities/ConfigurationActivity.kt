package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.DataStoreManager.Companion.showAlarmDismissedKey
import data.DataStoreManager.Companion.showTimeToBedKey
import data.DataStoreManager.Companion.simpleStyleKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.SleepMessagesTarget
import targets.SleepMessagesTarget.Companion.dataStore

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val simpleStyle = context.dataStore.get(simpleStyleKey) ?: false
            val showTimeToBed = context.dataStore.get(showTimeToBedKey) ?: true
            val showAlarmDismissed = context.dataStore.get(showAlarmDismissedKey) ?: true

            PluginTheme {
                PreferenceLayout(title = stringResource(id = R.string.config_title)) {
                    PreferenceHeading(stringResource(id = R.string.messages_target_title))

                    PreferenceSwitch(
                        icon = R.drawable.cog,
                        title = stringResource(id = R.string.messages_target_simple_style),
                        description = stringResource(id = R.string.messages_target_simple_style_description),
                        onCheckedChange = {
                            value -> context.dataStore.save(simpleStyleKey, value)
                            SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                        },
                        checked = simpleStyle
                    )

                    PreferenceSwitch(
                        icon = R.drawable.bed_outline,
                        title = stringResource(id = R.string.messages_target_show_time_to_bed),
                        description = stringResource(id = R.string.messages_target_show_time_to_bed_description),
                        onCheckedChange = {
                            value -> context.dataStore.save(showTimeToBedKey, value)
                            SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                        },
                        checked = showTimeToBed
                    )

                    PreferenceSwitch(
                        icon = R.drawable.tea_outline,
                        title = stringResource(id = R.string.messages_target_show_alarm_dismissed),
                        description = stringResource(id = R.string.messages_target_show_alarm_dismissed_description),
                        onCheckedChange = {
                            value -> context.dataStore.save(showAlarmDismissedKey, value)
                            SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                        },
                        checked = showAlarmDismissed
                    )
                }
            }
        }
    }
}