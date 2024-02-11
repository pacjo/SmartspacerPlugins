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

class BatteryLevelComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get data
        val dataObject = jsonObject.getJSONObject("local_data")
        val level = dataObject.optInt("level", -1)

        return listOf(
            ComplicationTemplate.Basic(
                id = "example_$smartspacerId",
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.battery
                    )
                ),
                content = Text("$level%"),
                onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
            ).create()
        )
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Battery level complication",
            description = "Shows battery level",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.battery"
        )
    }
}