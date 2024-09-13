package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import data.SharedDataStoreManager.Companion.batteryCurrentKey
import data.SharedDataStoreManager.Companion.batteryIsChargingKey
import data.SharedDataStoreManager.Companion.batteryVoltageKey
import data.SharedDataStoreManager.Companion.disableTrimmingKey
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import providers.BatteryBroadcastProvider.Companion.dataStore
import ui.activities.ChargingComplicationConfigurationActivity

class ChargingStatusComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val isCharging = provideContext().dataStore.get(batteryIsChargingKey) ?: false
        val current = provideContext().dataStore.get(batteryCurrentKey) ?: 0
        val voltage = provideContext().dataStore.get(batteryVoltageKey) ?: 0

        val disableComplicationTextTrimming = provideContext().dataStore.get(disableTrimmingKey)

        return if (isCharging) {
            listOf(
                ComplicationTemplate.Basic(
                    id = "charging_complication_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            provideContext(),
                            R.drawable.battery_charging
                        )
                    ),
                    content = Text(
                        when {
                            voltage != 0 && current != 0 -> "${current/1000} mA, ${(voltage/100)/10f} V"
                            current == 0 -> "${(voltage/100)/10f} V"

                            else -> "failed to get battery stats"
                        }
                    ),
                    onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)),
                    trimToFit = when (disableComplicationTextTrimming) {
                        false -> TrimToFit.Enabled
                        else -> TrimToFit.Disabled
                    }
                ).create()
            )
        } else emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Charging status complication",
            description = "Shows voltage and current when charging",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_charging),
            configActivity = Intent(context, ChargingComplicationConfigurationActivity::class.java),
            broadcastProvider = "${BuildConfig.APPLICATION_ID}.broadcast.battery"
        )
    }
}