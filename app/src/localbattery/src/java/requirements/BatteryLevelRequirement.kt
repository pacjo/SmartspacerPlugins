package requirements

import android.content.Intent
import android.graphics.drawable.Icon
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider
import data.SharedDataStoreManager.Companion.batteryLevelKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import providers.BatteryBroadcastProvider.Companion.dataStore
import ui.activities.BatteryLevelRequirementConfigurationActivity

class BatteryLevelRequirement: SmartspacerRequirementProvider() {

    override fun isRequirementMet(smartspacerId: String): Boolean {
        val batteryLevel = provideContext().dataStore.get(batteryLevelKey)
        val comparisonLevel = provideContext().dataStore.get(createBatteryLevelDataStoreKey(smartspacerId))

        if (batteryLevel == null || comparisonLevel == null)
            return false

        return batteryLevel <= comparisonLevel
    }

    override fun getConfig(smartspacerId: String?): Config {
        val description = smartspacerId?.let { id ->
            provideContext().dataStore.get(createBatteryLevelDataStoreKey(id))?.let { level ->
                "Only show when device battery level is under $level%"
            }
        } ?: "Only show when device battery is in the specified range"

        return Config(
            label = "Battery level requirement",
            description = description,
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_unknown),
            configActivity = Intent(context, BatteryLevelRequirementConfigurationActivity::class.java)
        )
    }
}

fun createBatteryLevelDataStoreKey(smartspacerId: String): Preferences.Key<Int> {
    return intPreferencesKey("$smartspacerId:requirement:battery_level")
}