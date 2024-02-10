package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.icu.text.SimpleDateFormat
import androidx.core.graphics.drawable.IconCompat
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.CarouselTemplateData
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import utils.WeatherData
import utils.temperatureUnitConverter
import utils.weatherDataToIcon
import java.io.File

class GenericWeatherTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val targetUnit = preferences.optString("target_unit", "C")
        val targetStyle = preferences.optString("target_style","both")
        val launchPackage = preferences.optString("target_launch_package", "")
        val dataSource = preferences.optString("target_data_source", "hourly")
        val dataPoints = preferences.optInt("target_points_visible", 4)

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val weatherData = gson.fromJson(weather, WeatherData::class.java)

            val currentTemperature = weatherData.currentTemp
            val location = weatherData.location
            val currentCondition = weatherData.currentCondition

            val hourlyData = weatherData.hourly
//            val dailyForecasts = weatherData.forecasts
//
//            val forecast = when (dataSource) {
//                "daily" -> weatherData.forecasts
//                else -> weatherData.hourly
//            }

            if (dataPoints > 0) {

                val carouselItemList =         // TODO: allow switching daily <-> hourly
                    List(hourlyData.take(dataPoints).size) { index ->
                        val carouselItem = CarouselTemplateData.CarouselItem(
                            Text(temperatureUnitConverter(hourlyData[index].temp, targetUnit)),
                            Text(SimpleDateFormat("HH:mm ").format(hourlyData[index].timestamp * 1000L)),
                            com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                                IconCompat.createWithBitmap(
                                    Bitmap.createScaledBitmap(
                                        BitmapFactory.decodeResource(
                                            resources,
                                            weatherDataToIcon(weatherData, 1, index)
                                        ),
                                        50,
                                        50,
                                        true
                                    )
                                ).toIcon(context),
                                shouldTint = false
                            ), null
                        )
                        carouselItem
                    }

                return listOf(TargetTemplate.Carousel(
                    id = "example_$smartspacerId",
                    componentName = ComponentName(context!!, GenericWeatherTarget::class.java),
                    title = Text(location),
                    subtitle = Text(when (targetStyle) {
                        "condition" -> weatherData.currentCondition
                        "both" -> "${temperatureUnitConverter(weatherData.currentTemp, targetUnit)} ${weatherData.currentCondition}"
                        else -> temperatureUnitConverter(weatherData.currentTemp, targetUnit)
                    }),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithBitmap(
                            Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(
                                    resources,
                                    weatherDataToIcon(weatherData, 0)
                                ),
                                25,
                                25,
                                true
                            )
                        ).toIcon(context),
                        shouldTint = false
                    ),
                    items = carouselItemList,
                    onClick = when (context!!.packageManager.getLaunchIntentForPackage(launchPackage)) {
                        null -> null
                        else -> TapAction(
                            intent = Intent(context!!.packageManager.getLaunchIntentForPackage(launchPackage))
                        )
                    },
                    onCarouselClick = when (context!!.packageManager.getLaunchIntentForPackage(launchPackage)) {
                        null -> null
                        else -> TapAction(
                            intent = Intent(context!!.packageManager.getLaunchIntentForPackage(launchPackage))
                        )
                    }
                ).create().apply {
                    canBeDismissed = false
                })
            } else {
                return listOf(TargetTemplate.Basic(
                    id = "example_$smartspacerId",
                    componentName = ComponentName(context!!, GenericWeatherTarget::class.java),
                    title = Text(location),
                    subtitle = Text(when (targetStyle) {
                        "condition" -> weatherData.currentCondition
                        "both" -> "${temperatureUnitConverter(weatherData.currentTemp, targetUnit)} ${weatherData.currentCondition}"
                        else -> temperatureUnitConverter(weatherData.currentTemp, targetUnit)
                    }),
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
                    onClick = when (context!!.packageManager.getLaunchIntentForPackage(launchPackage)) {
                        null -> null
                        else -> TapAction(
                            intent = Intent(context!!.packageManager.getLaunchIntentForPackage(launchPackage))
                        )
                    }
                ).create())
            }
        } else {
            return listOf(TargetTemplate.Basic(
                id = "example_$smartspacerId",
                componentName = ComponentName(context!!, GenericWeatherTarget::class.java),
                title = Text("Couldn't get weather"),
                subtitle = Text("Enable integration in weather app, then sync"),
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        context,
                        R.drawable.alert_circle
                    )
                )
            ).create())
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Generic weather",
            description = "Shows weather information from supported apps",
            icon = Icon.createWithResource(context, R.drawable.weather_sunny_alert),
            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.genericweather.providers.weather"
        )
    }

    // TODO: consider removal (onProviderRemoved)

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }

}
