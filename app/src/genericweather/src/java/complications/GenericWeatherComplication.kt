package complications

import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.IconCompat
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.annotations.LimitedNativeSupport
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import utils.Temperature
import utils.WeatherData
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
        val unit = preferences.optString("unit", "C")
        val complicationStyle = preferences.optString("condition_complication_style","temperature")
        val complicationTrimToFit = preferences.optBoolean("condition_complication_trim_to_fit",true)
        val launchPackage = preferences.optString("launch_package", "")

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
                        "both" -> "${Temperature(data.currentTemp, unit)} ${data.currentCondition}"
                        else -> Temperature(data.currentTemp, unit).toString()
                    }),
                    onClick = getPackageLaunchTapAction(provideContext(), launchPackage),
                    trimToFit = when (complicationTrimToFit) {
                        false -> TrimToFit.Disabled
                        else -> TrimToFit.Enabled
                    }).create().apply {
                        weatherData = SmartspacerWeatherData(
                            description = data.currentCondition,
                            state = weatherDataToSmartspacerToIcon(data, 0),
                            useCelsius = when {
                                (unit == "F") -> false
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
            icon = Icon.createWithResource(context, R.drawable.weather_sunny_alert),
            configActivity = Intent(context, ConfigurationActivity::class.java)
        )
    }

}