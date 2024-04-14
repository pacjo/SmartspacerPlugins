package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.convertTimeTo
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import java.io.File

class LocalBatteryTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        isFirstRun(context!!)

        val file = File(context?.filesDir, "data.json")

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferencesObject = jsonObject.getJSONObject("preferences")
        val showEstimate = preferencesObject.optBoolean("target_show_estimate", true)
        val disableComplications = preferencesObject.optBoolean("target_disable_complications", false)

        // get data
        val dataObject = jsonObject.getJSONObject("local_data")
        val isCharging = dataObject.optBoolean("isCharging", false)
        val chargingTimeRemaining = dataObject.optLong("chargingTimeRemaining", 0)
        val level = dataObject.optInt("level", -1)

        val title = "Charging"

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
                onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)),
                subComplication = when (disableComplications) {
                    true -> ComplicationTemplate.blank().create()
                    else -> null
                }
            ).create().apply {
                canBeDismissed = false
            })
        } else emptyList()
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