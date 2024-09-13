package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.provider.Settings
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.SharedDataStoreManager.Companion.batteryChargingTimeRemainingKey
import data.SharedDataStoreManager.Companion.batteryIsChargingKey
import data.SharedDataStoreManager.Companion.batteryLevelKey
import data.SharedDataStoreManager.Companion.lowBatteryDismissedKey
import data.SharedDataStoreManager.Companion.showEstimateKey
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import providers.BatteryBroadcastProvider.Companion.dataStore
import ui.activities.StatusTargetConfigurationActivity

class LocalBatteryTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        // get preferences
        val showEstimate = provideContext().dataStore.get(showEstimateKey) ?: true

        // get data
        val isCharging = provideContext().dataStore.get(batteryIsChargingKey) ?: false
        val chargingTimeRemaining = provideContext().dataStore.get(batteryChargingTimeRemainingKey) ?: 0
        val level = provideContext().dataStore.get(batteryLevelKey) ?: -1
        val isLowBatteryDismissed = provideContext().dataStore.get(lowBatteryDismissedKey) ?: false

        // get battery saver trigger level (and set a default value, in case it's unset)
        val batterySaverTriggerLevel = try {
            Settings.Global.getInt(context!!.contentResolver, "low_power_trigger_level")
        } catch (e: Settings.SettingNotFoundException) {
            15
        }

        // reset the dismissed status (writing null removes entry)
        if (level > batterySaverTriggerLevel)
            provideContext().dataStore.save(lowBatteryDismissedKey)

        val title = when {
            isCharging -> "Charging"
            level <= batterySaverTriggerLevel && !isLowBatteryDismissed -> "Battery low ($level %)"
            else -> ""
        }

        val subtitle = when (showEstimate && (isCharging && chargingTimeRemaining > -1)) {
            false -> "$level%"        // if estimate is disabled by user or we don't have it
            else -> when (level == 100) {
                true -> "$level% — charging complete"
                else -> "$level% — full in ${Time.getTimeToEvent(chargingTimeRemaining)}"
            }
        }

        return if (isCharging) {
            listOf(TargetTemplate.Basic(
                id = "charging_target_$smartspacerId",
                componentName = ComponentName(provideContext(), LocalBatteryTarget::class.java),
                title = Text(title),
                subtitle = Text(subtitle),
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.baseline_bolt
                    )
                ),
                onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
            ).create().apply {
                canBeDismissed = false
            })
        } else if (level <= batterySaverTriggerLevel && !isLowBatteryDismissed) {
            listOf(TargetTemplate.Basic(
                id = "low_battery_target_$smartspacerId",
                componentName = ComponentName(provideContext(), LocalBatteryTarget::class.java),
                title = Text(title),
                subtitle = null,
                icon = null,
                onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
            ).create().apply {
                canTakeTwoComplications = true
            })
        } else emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Battery info",
            description = "Shows battery related messages",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_unknown),
            configActivity = Intent(context, StatusTargetConfigurationActivity::class.java),
            broadcastProvider = "${BuildConfig.APPLICATION_ID}.broadcast.battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        context?.let {
            provideContext().dataStore.save(lowBatteryDismissedKey, true)
            notifyChange(smartspacerId)

            return true
        }

        return false
    }

}