package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.StatusTargetDataStoreManager.Companion.DATASTORE_NAME
import data.StatusTargetDataStoreManager.Companion.showEstimateKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore
import org.json.JSONObject
import ui.activities.StatusTargetConfigurationActivity
import java.io.File

class LocalBatteryTarget: SmartspacerTargetProvider() {

    companion object {
        // during the transition period we'll be using both json and datastore
        // over time we'll move everything to datastore, TODO: remove comment
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
    }

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        isFirstRun(context!!)

        val file = File(context?.filesDir, "data.json")

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val showEstimate = provideContext().dataStore.get(showEstimateKey) ?: true

        // get data
        val dataObject = jsonObject.getJSONObject("local_data")
        val isCharging = dataObject.optBoolean("isCharging", false)
        val chargingTimeRemaining = dataObject.optLong("chargingTimeRemaining", 0)
        val level = dataObject.optInt("level", -1)
        val isLowBatteryDismissed = getBoolFromDataStore(provideContext().dataStore, "low_battery_dismissed") ?: false

        // get battery saver trigger level (and set a default value, in case it's unset)
        val batterySaverTriggerLevel = try {
            Settings.Global.getInt(context!!.contentResolver, "low_power_trigger_level")
        } catch (e: Settings.SettingNotFoundException) {
            15
        }

        // reset the dismissed status (writing null removes entry)
        if (level > batterySaverTriggerLevel)
            saveToDataStore(provideContext().dataStore, "low_battery_dismissed")

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
                id = "example_$smartspacerId",
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
                id = "example_$smartspacerId",
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
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        context?.let {
            saveToDataStore(it.dataStore, "low_battery_dismissed", true)
            notifyChange(smartspacerId)

            return true
        }

        return false
    }

}