package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.StatusTargetDataStoreManager.Companion.showEstimateKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.LocalBatteryTarget
import targets.LocalBatteryTarget.Companion.dataStore

class StatusTargetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val smartspacerId = intent.getStringExtra(SmartspacerConstants.EXTRA_SMARTSPACER_ID)

            val showEstimate = dataStore.get(showEstimateKey) ?: true

            PluginTheme {
                PreferenceLayout("Local Battery") {
                    PreferenceHeading("Status target")

                    PreferenceSwitch(
                        icon = R.drawable.clock_time_ten_outline,
                        title = "Charging estimate",
                        description = "Show time to charge",
                        onCheckedChange = { value ->
                            dataStore.save(showEstimateKey, value)

                            SmartspacerTargetProvider.notifyChange(
                                context = context,
                                provider = LocalBatteryTarget::class.java,
                                smartspacerId = smartspacerId!!
                            )
                        },
                        checked = showEstimate
                    )
                }
            }
        }
    }
}