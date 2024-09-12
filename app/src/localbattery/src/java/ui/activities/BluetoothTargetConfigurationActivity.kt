package ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.BluetoothTargetDataStoreManager.Companion.dismissedMACsKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceButton
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.BluetoothBatteryTarget
import targets.BluetoothBatteryTarget.Companion.dataStore

class BluetoothTargetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val smartspacerId = intent.getStringExtra(SmartspacerConstants.EXTRA_SMARTSPACER_ID)
            
            PluginTheme {
                PreferenceLayout("Local Battery") {
                    PreferenceHeading("Bluetooth target")

                    PreferenceButton(
                        icon = R.drawable.restore,
                        title = "Reset dismissed devices",
                        description = "Clears all dismissed devices",
                        onClick = {
                            dataStore.save(dismissedMACsKey)

                            SmartspacerTargetProvider.notifyChange(
                                context = context,
                                provider = BluetoothBatteryTarget::class.java,
                                smartspacerId = smartspacerId!!
                            )

                            Toast.makeText(
                                context,
                                "Devices reset successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}