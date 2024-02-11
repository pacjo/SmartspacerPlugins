package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import java.io.File

class ChargingStatusComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get data
        val dataObject = jsonObject.getJSONObject("local_data")
        val isCharging = dataObject.optBoolean("isCharging", false)
        val current = dataObject.optInt("current", 0)
        val voltage = dataObject.optInt("voltage", 0)

        return if (isCharging) {
            listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            provideContext(),
                            R.drawable.battery_charging
                        )
                    ),
                    content = Text(when {
                        voltage != 0 && current != 0 -> "$current mA@${(voltage/100)/10f} V"
                        current == 0 -> "${(voltage/100)/10f} V"
                        else -> "failed to get battery stats"
                    }),
                    onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
                ).create()
            )
        } else emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Charging status complication",
            description = "Shows voltage and current when charging",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_charging),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.battery"
        )
    }
}