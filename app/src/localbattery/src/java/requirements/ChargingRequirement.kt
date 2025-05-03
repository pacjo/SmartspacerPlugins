package requirements

import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider
import data.SharedDataStoreManager.Companion.batteryIsChargingKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import providers.BatteryBroadcastProvider.Companion.dataStore

class ChargingRequirement: SmartspacerRequirementProvider() {

    override fun isRequirementMet(smartspacerId: String): Boolean {
        return provideContext().dataStore.get(batteryIsChargingKey) == true
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Charging requirement",
            description = "Only show when device is charging",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_charging)
        )
    }
}