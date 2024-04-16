package receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.GenericSunTimesComplication
import complications.GenericWeatherComplication
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import targets.GenericWeatherTarget
import java.io.File

class WeatherBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // save data to file
        val weatherData = intent.getStringExtra("WeatherJson")
        val file = File(context.filesDir, "data.json")

        if (weatherData != null) {
            isFirstRun(context)

            // Read JSON
            val jsonObject = JSONObject(file.readText())

            // Update only the "weather" key
            jsonObject.put("weather", JSONObject(weatherData))

            file.writeText(jsonObject.toString())
        }

        SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
        SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
        SmartspacerComplicationProvider.notifyChange(context, GenericSunTimesComplication::class.java)
    }
}