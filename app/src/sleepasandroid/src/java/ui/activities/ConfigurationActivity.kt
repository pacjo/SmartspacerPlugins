package ui.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.theme.getColorScheme
import nodomain.pacjo.smartspacer.plugin.utils.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.utils.SettingsTopBar
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

            MaterialTheme (
                // Change default colorScheme to our dynamic one
                colorScheme = getColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {

                        SettingsTopBar((context as? Activity)!!,"Sleep As Android")

                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Messages target",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            PreferenceSwitch(
                                icon = R.drawable.cog,
                                title = "Simple style",
                                subtitle = "Show basic target instead\n of image one",
                                stateCallback = {
                                    value -> saveToDataStore(context.dataStore,"simple_style", value)
                                    SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                                },
                                checked = simpleStyle
                            )

                            PreferenceSwitch(
                                icon = R.drawable.bed_outline,
                                title = "Time to bed",
                                subtitle = "Show time to bed message",
                                stateCallback = {
                                    value -> saveToDataStore(context.dataStore,"show_time_to_bed", value)
                                    SmartspacerTargetProvider.notifyChange(context, SleepMessagesTarget::class.java)
                                },
                                checked = showTimeToBed
                            )

                            PreferenceSwitch(
                                icon = R.drawable.tea_outline,
                                title = "Alarm dismissed",
                                subtitle = "Show alarm dismissed message",
                                stateCallback = {
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
    }
}