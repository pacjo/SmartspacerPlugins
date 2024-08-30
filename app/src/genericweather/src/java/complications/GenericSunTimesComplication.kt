package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import utils.WeatherData
import java.io.File
import kotlin.math.min

class GenericSunTimesComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(provideContext())
        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val complicationTrimToFit = preferences.optBoolean("suntimes_complication_trim_to_fit",true)
        val launchPackage = preferences.optString("launch_package", "")

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val weatherData = gson.fromJson(weather, WeatherData::class.java)

            val nextSunrise: Long = when (System.currentTimeMillis() < weatherData.sunRise * 1000L) {
                true -> weatherData.sunRise                 // if we're still before today's sunrise
                else -> weatherData.forecasts[0].sunRise
            } * 1000L           // broken without this

            val nextSunset: Long = when (System.currentTimeMillis() < weatherData.sunSet * 1000L) {
                true -> weatherData.sunSet                 // if we're still before today's sunset
                else -> weatherData.forecasts[0].sunSet
            } * 1000L           // broken without this

            // so, we have next sunrise and sunset
            // we'll always show next event (relative to the current time)
            // so it'll be min(nextSunrise, nextSunset)

            val nextEvent = min(nextSunrise, nextSunset)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            context,
                            when (nextEvent == nextSunrise) {
                                true -> R.drawable.ic_sunrise
                                else -> R.drawable.ic_sunset
                            }
                        )
                    ),
                    content = Text(Time(provideContext(), nextEvent).getEventTime()),
                    onClick = when (context!!.packageManager.getLaunchIntentForPackage(launchPackage)) {
                        null -> null
                        else -> TapAction(
                            intent = Intent(context!!.packageManager.getLaunchIntentForPackage(launchPackage))
                        )
                    },
                    trimToFit = when (complicationTrimToFit) {
                        false -> TrimToFit.Disabled
                        else -> TrimToFit.Enabled
                    }
                ).create()
            )
        } else {
            // If nothing was returned above
            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            context,
                            R.drawable.alert_circle
                        )
                    ),
                    content = Text("No data"),
                    onClick = null
                ).create()
            )
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Generic sun times",
            description = "Shows sunrise / sunset information from supported apps",
            icon = Icon.createWithResource(context, R.drawable.ic_sunrise),
            configActivity = Intent(context, ConfigurationActivity::class.java)
        )
    }

}