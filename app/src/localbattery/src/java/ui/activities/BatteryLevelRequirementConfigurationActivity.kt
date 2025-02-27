package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSlider
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import providers.BatteryBroadcastProvider.Companion.dataStore
import requirements.BatteryLevelRequirement
import requirements.createBatteryLevelDataStoreKey

class BatteryLevelRequirementConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val smartspacerId = intent.getStringExtra(SmartspacerConstants.EXTRA_SMARTSPACER_ID)

            var comparisonLevel = context.dataStore.get(createBatteryLevelDataStoreKey(smartspacerId!!))

            PluginTheme {
                PreferenceLayout("Local Battery") {
                    PreferenceHeading("Battery level requirement")

                    PreferenceSlider(
                        icon = R.drawable.compare_horizontal,
                        title = "Battery level",
                        description = "Battery level which will met the requirement",
                        onSliderChange = { value ->
                            comparisonLevel = value
                            dataStore.save(createBatteryLevelDataStoreKey(smartspacerId), comparisonLevel)

                            SmartspacerRequirementProvider.notifyChange(
                                context = context,
                                provider = BatteryLevelRequirement::class.java,
                                smartspacerId = smartspacerId
                            )
                        },
                        range = 0..100,
                        defaultPosition = comparisonLevel ?: 50
                    )
                }
            }
        }
    }
}