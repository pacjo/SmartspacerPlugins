package providers

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import targets.SleepMessagesTarget
import java.io.File

class SleepBroadcastProvider: SmartspacerBroadcastProvider() {

    override fun onReceive(intent: Intent) {
        // save data to file
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        // Read JSON
        val jsonObject = JSONObject(file.readText())

        // Update only the "event" key
        jsonObject.put("event", intent.action.toString())

        file.writeText(jsonObject.toString())

        SmartspacerTargetProvider.notifyChange(context!!, SleepMessagesTarget::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(
                IntentFilter("com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO"),
                IntentFilter("com.urbandroid.sleep.alarmclock.ALARM_ALERT_DISMISS_AUTO")
            )
        )
    }

}