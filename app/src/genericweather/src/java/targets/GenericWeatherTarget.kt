package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toIcon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.CarouselTemplateData
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import utils.Temperature
import utils.WeatherData
import utils.weatherDataToIcon
import java.io.File

class GenericWeatherTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(provideContext())

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val unit = preferences.optString("unit", "C")
        val targetStyle = preferences.optString("target_style","both")
        val launchPackage = preferences.optString("launch_package", "")
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

            if (dataPoints > 0) {
                // TODO: allow switching daily <-> hourly
                val carouselItemList = mutableListOf<CarouselTemplateData. CarouselItem>()

                hourlyData.take(dataPoints).forEachIndexed { index, point ->
                    val carouselItem = CarouselTemplateData.CarouselItem(
                        Text(Temperature(point.temp, unit).toString()),
                        Text(" ${Time(provideContext(), point.timestamp).getEventTime()} "),
                        com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            Bitmap.createScaledBitmap(
                                ContextCompat.getDrawable(
                                    provideContext(),
                                    weatherDataToIcon(provideContext(), weatherData, 1, index)
                                )!!.toBitmap(),
                                (24 * resources.displayMetrics.density).toInt(),
                                (24 * resources.displayMetrics.density).toInt(),
                                true
                            ).toIcon(),
                            shouldTint = false
                        ), null
                    )

                    carouselItemList.add(carouselItem)
                }

                return listOf(TargetTemplate.Carousel(
                    id = "example_$smartspacerId",
                    componentName = ComponentName(context!!, GenericWeatherTarget::class.java),
                    title = Text(location),
                    subtitle = Text(when (targetStyle) {
                        "condition" -> currentCondition
                        "both" -> "${Temperature(currentTemperature, unit)} $currentCondition"
                        else -> Temperature(currentTemperature, unit).toString()
                    }),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithResource(
                            provideContext(),
                            weatherDataToIcon(provideContext(), weatherData, 0)
                        ).toIcon(context),
                        shouldTint = false
                    ),
                    items = carouselItemList,
                    // TODO: export as utility functions
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
                        "condition" -> currentCondition
                        "both" -> "${Temperature(currentTemperature, unit)} $currentCondition"
                        else -> Temperature(currentTemperature, unit).toString()
                    }),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithResource(
                            provideContext(),
                            weatherDataToIcon(provideContext(), weatherData, 0)
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
            configActivity = Intent(context, ConfigurationActivity::class.java)
        )
    }

    // TODO: consider removal (onProviderRemoved)

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }

}
