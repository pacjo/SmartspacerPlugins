package complications

import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.IconCompat
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.annotations.LimitedNativeSupport
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import utils.WeatherData
import utils.temperatureUnitConverter
import utils.weatherDataToIcon
import utils.weatherDataToSmartspacerToIcon
import java.io.File
import com.kieronquinn.app.smartspacer.sdk.model.weather.WeatherData as SmartspacerWeatherData

class GenericWeatherComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class, LimitedNativeSupport::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(provideContext())
        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val complicationUnit = preferences.optString("condition_complication_unit", "C")
        val complicationStyle = preferences.optString("condition_complication_style","temperature")
        val complicationTrimToFit = preferences.optBoolean("condition_complication_trim_to_fit",true)
        val launchPackage = preferences.optString("condition_complication_launch_package", "")

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val data = gson.fromJson(weather, WeatherData::class.java)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithResource(
                            provideContext(),
                            weatherDataToIcon(provideContext(), data, 0)
                        ).toIcon(context),
                        shouldTint = false
                    ),
                    content = Text(when (complicationStyle) {
                        "condition" -> data.currentCondition
                        "both" -> "${temperatureUnitConverter(data.currentTemp, complicationUnit)} ${data.currentCondition}"
                        else -> temperatureUnitConverter(data.currentTemp, complicationUnit)
                    }),
                    onClick = when (context!!.packageManager.getLaunchIntentForPackage(launchPackage)) {
                        null -> null
                        else -> TapAction(
                            intent = Intent(context!!.packageManager.getLaunchIntentForPackage(launchPackage))
                        )
                    },
                    trimToFit = when (complicationTrimToFit) {
                        false -> TrimToFit.Disabled
                        else -> TrimToFit.Enabled
                    }).create().apply {
                        weatherData = SmartspacerWeatherData(
                            description = data.currentCondition,
                            state = weatherDataToSmartspacerToIcon(data, 0),
                            useCelsius = when {
                                (complicationUnit == "F") -> false
                                else -> true
                            },
                            temperature = data.currentTemp
                    )
                }
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
            label = "Generic weather",
            description = "Shows temperature and/or condition icon from supported apps",
            icon = Icon.createWithResource(context, R.drawable.weather_sunny_alert),     // TODO: fix small size in smartspacer
            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.genericweather.providers.weather"
        )
    }

}