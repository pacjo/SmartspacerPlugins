package requirements

import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import java.io.File

class ChargingRequirement: SmartspacerRequirementProvider() {

    override fun isRequirementMet(smartspacerId: String): Boolean {
        isFirstRun(context!!)

        val file = File(context?.filesDir, "data.json")

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get data
        val dataObject = jsonObject.getJSONObject("local_data")

        return dataObject.optBoolean("isCharging", false)
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Charging requirement",
            description = "Only show when device is charging",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_charging)
        )
    }
}