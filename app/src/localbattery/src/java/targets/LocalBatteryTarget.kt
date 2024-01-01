package targets

import android.content.ComponentName
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.BatteryManager
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.convertTimeTo
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import java.io.File

class LocalBatteryTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val showEstimate = preferences.optBoolean("target_show_estimate", true)

        val batteryManager = context?.getSystemService(BATTERY_SERVICE) as BatteryManager
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context!!.registerReceiver(null, ifilter)
        }

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING
        val chargingTimeRemaining = batteryManager.computeChargeTimeRemaining()
        val level = (
                (
                        (100f *
                                batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)!!) /
                                batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                        )
                ).toInt()

        val title = "Charging"

        val subtitle = when (showEstimate && (charging && chargingTimeRemaining > -1)) {
            false -> "${level}%"        // if estimate is user-disable or we don't have it
            else -> when (level == 100) {
                true -> "${level}% — charging complete"
                else -> "${level}% — full in ${convertTimeTo(chargingTimeRemaining)}"
            }
        }

        if (charging) {
            return listOf(TargetTemplate.Basic(
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
        } else {
            return emptyList()
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Charging info",
            description = "Shows charging information",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_unknown),
            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }

}