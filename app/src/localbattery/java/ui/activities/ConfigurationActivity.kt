package ui.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                            modifier = Modifier.verticalScroll(rememberScrollState())

                        ) {                        // TODO: check lazycolumn
                            Divider()
                            Text("Charging target")

                            PreferenceSwitch(
                                icon = R.drawable.baseline_bolt,   // TODO: change
                                title = "Charging estimate",
                                subtitle = "Show time to charge",
                                stateCallback = {
                                    value -> savePreference(context,"target_show_estimate", value)
                                    SmartspacerTargetProvider.notifyChange(context, LocalBatteryTarget::class.java)
                                },
                                checked = showEstimate
                            )
                        }
                    }
                }
            }
        }
    }
}

