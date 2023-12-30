package providers

import android.content.Intent
import android.content.IntentFilter
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.GenericSunTimesComplication
import complications.GenericWeatherComplication
import targets.GenericWeatherTarget
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import java.io.File

class GenericWeatherProvider: SmartspacerBroadcastProvider() {

    override fun onReceive(intent: Intent) {
        // save data to file
        val weatherData = intent.getStringExtra("WeatherJson")
        val file = File(context?.filesDir, "data.json")

        if (weatherData != null) {
            isFirstRun(context!!)

            // Read JSON
            val jsonObject = JSONObject(file.readText())

            // Update only the "weather" key
            jsonObject.put("weather", JSONObject(weatherData))

            file.writeText(jsonObject.toString())
        }

        SmartspacerTargetProvider.notifyChange(context!!, GenericWeatherTarget::class.java)
        SmartspacerComplicationProvider.notifyChange(context!!, GenericWeatherComplication::class.java)
        SmartspacerComplicationProvider.notifyChange(context!!, GenericSunTimesComplication::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(IntentFilter("nodomain.freeyourgadget.gadgetbridge.ACTION_GENERIC_WEATHER"))
        )
    }
}