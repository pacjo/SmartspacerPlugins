package providers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.BatteryLevelComplication
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import targets.LocalBatteryTarget
import java.io.File

class BatteryBroadcastProvider: SmartspacerBroadcastProvider() {

    override fun onReceive(intent: Intent) {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        val dataObject = JSONObject()

        val batteryManager = context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context!!.registerReceiver(null, ifilter)
        }

        val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        val isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL ||
            batteryManager.isCharging
        val chargingTimeRemaining = batteryManager.computeChargeTimeRemaining()
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val level = (
            (
                (
                    100f *
                    batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)!!) /
                    batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                )
            ).toInt()

        // save result to file
        dataObject.put("status", status)
        dataObject.put("isCharging", isCharging)
        dataObject.put("chargingTimeRemaining", chargingTimeRemaining)
        dataObject.put("voltage", voltage)
        dataObject.put("current", current)
        dataObject.put("level", level)

        jsonObject.put("local_data", dataObject)
        file.writeText(jsonObject.toString())

        // notify about change
        SmartspacerTargetProvider.notifyChange(context!!, LocalBatteryTarget::class.java)
//        SmartspacerComplicationProvider.notifyChange(context!!, ChargingStatusComplication::class.java)
        SmartspacerComplicationProvider.notifyChange(context!!, BatteryLevelComplication::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        )
    }

}