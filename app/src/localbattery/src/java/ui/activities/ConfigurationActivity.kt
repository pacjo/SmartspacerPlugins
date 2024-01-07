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
import targets.LocalBatteryTarget
import nodomain.pacjo.smartspacer.plugin.ui.theme.getColorScheme
import nodomain.pacjo.smartspacer.plugin.utils.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.utils.SettingsTopBar
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.savePreference
import org.json.JSONObject
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
            val disableComplications = preferences.optBoolean("target_disable_complications", false)

            MaterialTheme (
                // Change default colorScheme to our dynamic one
                colorScheme = getColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {

                        SettingsTopBar((context as? Activity)!!,"Local Battery")

                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Charging target",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            PreferenceSwitch(
                                icon = R.drawable.clock_time_ten_outline,
                                title = "Charging estimate",
                                subtitle = "Show time to charge",
                                stateCallback = {
                                    value -> savePreference(context,"target_show_estimate", value)
                                    SmartspacerTargetProvider.notifyChange(context, LocalBatteryTarget::class.java)
                                },
                                checked = showEstimate
                            )

                            PreferenceSwitch(
                                icon = R.drawable.card_off_outline,
                                title = "Force no complications",
                                subtitle = "Disables complication spot",
                                stateCallback = {
                                    value -> savePreference(context,"target_disable_complications", value)
                                    SmartspacerTargetProvider.notifyChange(context, LocalBatteryTarget::class.java)
                                },
                                checked = disableComplications
                            )
                        }
                    }
                }
            }
        }
    }
}