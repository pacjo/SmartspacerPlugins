package complications

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.IconCompat
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.plugin.R
import ui.activities.ConfigurationActivity
import utils.WeatherData
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import utils.temperatureUnitConverter
import utils.weatherDataToIcon
import org.json.JSONObject
import java.io.File

class GenericWeatherComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)
        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val complicationUnit = preferences.optString("complication_unit", "C")
        val complicationStyle = preferences.optString("complication_style","temperature")
        val complicationTrimToFit = preferences.optBoolean("complication_trim_to_fit",true)
        val launchPackage = preferences.optString("complication_launch_package", "")

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val weatherData = gson.fromJson(weather, WeatherData::class.java)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithBitmap(
                            Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(
                                    resources,
                                    weatherDataToIcon(weatherData, 0)
                                ),
                                50,
                                50,
                                true
                            )
                        ).toIcon(context),
                        shouldTint = false
                    ),
                    content = Text(when (complicationStyle) {
                        "condition" -> weatherData.currentCondition
                        "both" -> "${temperatureUnitConverter(weatherData.currentTemp, complicationUnit)} ${weatherData.currentCondition}"
                        else -> temperatureUnitConverter(weatherData.currentTemp, complicationUnit)
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
                    }).create()
            )
        } else {
            // If nothing was returned above
            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            context,
                            R.drawable.baseline_error_24
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
            icon = Icon.createWithResource(context, R.drawable.ic_launcher_foreground),     // TODO: fix small size in smartspacer
            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.genericweather.providers.weather"
        )
    }

}