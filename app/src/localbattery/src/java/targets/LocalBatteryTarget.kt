package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.convertTimeTo
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import java.io.File

// during the transition period we'll be using both json and datastore
// over time we'll move everything to datastore, TODO: remove comment
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "status_target_data")

class LocalBatteryTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        isFirstRun(context!!)

        val file = File(context?.filesDir, "data.json")

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferencesObject = jsonObject.getJSONObject("preferences")
        val showEstimate = preferencesObject.optBoolean("target_show_estimate", true)

        // get data
        val dataObject = jsonObject.getJSONObject("local_data")
        val isCharging = dataObject.optBoolean("isCharging", false)
        val chargingTimeRemaining = dataObject.optLong("chargingTimeRemaining", 0)
        val level = dataObject.optInt("level", -1)
        val isLowBatteryDismissed = getBoolFromDataStore(context!!.dataStore, "low_battery_dismissed") ?: false

        // reset the dismissed status (writing null removes entry)
        if (level > 20)     // TODO: get this value from OS
            saveToDataStore(context!!.dataStore, "low_battery_dismissed")

        val title = when {
            isCharging -> "Charging"
            level <= 20 && !isLowBatteryDismissed -> "Battery low"
            else -> ""
        }

        val subtitle = when (showEstimate && (isCharging && chargingTimeRemaining > -1)) {
            false -> "${level}%"        // if estimate is user-disable or we don't have it
            else -> when (level == 100) {
                true -> "${level}% — charging complete"
                else -> "${level}% — full in ${convertTimeTo(chargingTimeRemaining)}"
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
        } else if (level <= 20 && !isLowBatteryDismissed) {   // TODO: we should get this value from the os instead
            listOf(TargetTemplate.Basic(
                id = "example_$smartspacerId",
                componentName = ComponentName(provideContext(), LocalBatteryTarget::class.java),
                title = Text(title),
                subtitle = null,
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.battery_low
                    )
                ),
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
            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        context?.let {
            saveToDataStore(it.dataStore, "low_battery_dismissed", true)
            notifyChange(context!!, LocalBatteryTarget::class.java)

            return true
        }

        return false
    }

}