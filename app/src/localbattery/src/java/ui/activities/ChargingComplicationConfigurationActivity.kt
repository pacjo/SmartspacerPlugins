package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.ChargingStatusComplication
import data.SharedDataStoreManager.Companion.disableTrimmingKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import providers.BatteryBroadcastProvider.Companion.dataStore

class ChargingComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val smartspacerId = intent.getStringExtra(SmartspacerConstants.EXTRA_SMARTSPACER_ID)

            val textTrimming = dataStore.get(disableTrimmingKey) ?: false

            PluginTheme {
                PreferenceLayout("Local Battery") {
                    PreferenceHeading("Charging complication")

                    PreferenceSwitch(
                        icon = R.drawable.content_cut,
                        title = "Disable Complication Text Trimming",
                        description = "Allows longer text in charging complication",
                        onCheckedChange = { value ->
                            dataStore.save(disableTrimmingKey, value)

                            SmartspacerComplicationProvider.notifyChange(
                                context = context,
                                provider = ChargingStatusComplication::class.java,
                                smartspacerId = smartspacerId!!
                            )
                        },
                        checked = textTrimming
                    )
                }
            }
        }
    }
}