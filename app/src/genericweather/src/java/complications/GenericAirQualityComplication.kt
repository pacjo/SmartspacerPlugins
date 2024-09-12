package complications

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Icon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import data.PreferencesKeys
import data.dataStore
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.AirQualityComplicationConfigurationActivity
import utils.AirQualityThresholds
import utils.WeatherData
import java.io.File

class GenericAirQualityComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(provideContext())
        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val complicationShowAlways = preferences.optBoolean("air_quality_complication_show_always", false)
        val launchPackage = provideContext().dataStore.get(PreferencesKeys.LAUNCH_PACKAGE) ?: ""

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val weatherData = gson.fromJson(weather, WeatherData::class.java)

            val aqi = weatherData.airQuality.aqi
            val aqiColor = getAQIColor(aqi)

            // TODO: make threshold configurable

            return if (aqi > AirQualityThresholds.FAIR || complicationShowAlways)
                listOf(
                    ComplicationTemplate.Basic(
                        id = "example_$smartspacerId",
                        icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            icon = createCircleIcon(aqiColor),
                            shouldTint = false
                        ),
                        content = Text("$aqi AQI"),
                        onClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                    ).create()
                ) else emptyList()
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
            label = "Generic air quality",
            description = "Shows air quality index information from supported apps",
            icon = Icon.createWithResource(context, R.drawable.weather_dust),
            configActivity = Intent(context, AirQualityComplicationConfigurationActivity::class.java)
        )
    }

    private fun createCircleIcon(color: Int): Icon {
        val size = 48
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        canvas.drawCircle(size / 2f, size / 2f, size / 4f, paint)

        return Icon.createWithBitmap(bitmap)
    }

    private fun getAQIColor(aqi: Int): Int {
        // colors are taken from EAQI from google maps API, because I liked them
        // https://developers.google.com/maps/documentation/air-quality/laqis

        return when {
            aqi <= AirQualityThresholds.EXCELLENT -> Color.parseColor("#50F0E6")
            aqi <= AirQualityThresholds.FAIR -> Color.parseColor("#50CCAA")
            aqi <= AirQualityThresholds.POOR -> Color.parseColor("#F0E641")
            aqi <= AirQualityThresholds.UNHEALTHY -> Color.parseColor("#FF5050")
            aqi <= AirQualityThresholds.VERY_UNHEALTHY -> Color.parseColor("#960032")
            else -> Color.parseColor("#7D2181")
        }
    }
}