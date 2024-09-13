package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toIcon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.CarouselTemplateData
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.DataStoreManager.Companion.conditionTargetDataPointsKey
import data.DataStoreManager.Companion.conditionTargetStyleKey
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.iconPackPackageNameKey
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.temperatureUnitKey
import data.DataStoreManager.Companion.weatherDataKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.ConditionTargetConfigurationActivity
import utils.Temperature
import utils.WeatherData
import utils.icons.BreezyIconProvider
import utils.icons.BuiltinIconProvider
import utils.icons.IconHelper.getWeatherIcon
import utils.icons.IconPackInfo

class WeatherConditionTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            // get preferences
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""
            val temperatureUnit = provideContext().dataStore.get(temperatureUnitKey) ?: "C"
            val iconPackPackageName = provideContext().dataStore.get(iconPackPackageNameKey)

            val targetStyle = provideContext().dataStore.get(conditionTargetStyleKey) ?: "both"
            val dataPoints = provideContext().dataStore.get(conditionTargetDataPointsKey) ?: 4

            // rewrite this icon code
            val iconProvider = BreezyIconProvider(provideContext())
            var iconPack: IconPackInfo? = null
            if (iconPackPackageName != null)
                iconPack = iconProvider.getIconPackByPackageName(iconPackPackageName)

            // TODO: throw this into utils
            val gson = Gson()
            val weatherData = gson.fromJson(jsonString, WeatherData::class.java)

            val currentTemperature = weatherData.currentTemp
            val location = weatherData.location
            val currentCondition = weatherData.currentCondition

            val hourlyData = weatherData.hourly

            if (dataPoints > 0) {
                // TODO: allow switching daily <-> hourly
                val carouselItemList = mutableListOf<CarouselTemplateData. CarouselItem>()

                hourlyData.take(dataPoints).forEachIndexed { index, point ->
                    val carouselItem = CarouselTemplateData.CarouselItem(
                        Text(Temperature(point.temp, temperatureUnit).toString()),
                        Text(" ${Time(provideContext(), point.timestamp).getEventTime()} "),
                        com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            // TODO: figure out scaling issue
                            // also icon pack icons look pixelated
                            (
                                if (iconPack != null)
                                    Bitmap.createScaledBitmap(
                                        iconProvider.getWeatherIcon(
                                            iconPack = iconPack,
                                            data = weatherData,
                                            type = 1,
                                            index = index
                                        ).toBitmap(),
                                        (12 * resources.displayMetrics.density).toInt(),
                                        (12 * resources.displayMetrics.density).toInt(),
                                        true
                                    )
                                else
                                    Bitmap.createScaledBitmap(
                                        ContextCompat.getDrawable(
                                            provideContext(),
                                            BuiltinIconProvider.getWeatherIcon(
                                                context = provideContext(),
                                                data = weatherData,
                                                type = 1,
                                                index = index
                                            )
                                        )!!.toBitmap(),
                                        (24 * resources.displayMetrics.density).toInt(),
                                        (24 * resources.displayMetrics.density).toInt(),
                                        true
                                    )
                            ).toIcon(),
                            shouldTint = false
                        ), null
                    )

                    carouselItemList.add(carouselItem)
                }

                return listOf(TargetTemplate.Carousel(
                    id = "example_$smartspacerId",
                    componentName = ComponentName(context!!, WeatherConditionTarget::class.java),
                    title = Text(location),
                    subtitle = Text(when (targetStyle) {
                        "condition" -> currentCondition
                        "both" -> "${Temperature(currentTemperature, temperatureUnit)} $currentCondition"
                        else -> Temperature(currentTemperature, temperatureUnit).toString()
                    }),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        getWeatherIcon(
                            context = provideContext(),
                            iconPackPackageName = iconPackPackageName,
                            weatherData = weatherData,
                            type = 0
                        ),
                        shouldTint = false
                    ),
                    items = carouselItemList,
                    onClick = getPackageLaunchTapAction(provideContext(), launchPackage),
                    onCarouselClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                ).create().apply {
                    canBeDismissed = false
                })
            } else {
                return listOf(TargetTemplate.Basic(
                    id = "example_$smartspacerId",
                    componentName = ComponentName(context!!, WeatherConditionTarget::class.java),
                    title = Text(location),
                    subtitle = Text(
                        when (targetStyle) {
                            "condition" -> currentCondition
                            "both" -> "${Temperature(currentTemperature, temperatureUnit)} $currentCondition"
                            else -> Temperature(currentTemperature, temperatureUnit).toString()
                        }
                    ),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        getWeatherIcon(
                            context = provideContext(),
                            iconPackPackageName = iconPackPackageName,
                            weatherData = weatherData,
                            type = 0
                        ),
                        shouldTint = false
                    ),
                    onClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                ).create())
            }
        } else {
            return listOf(TargetTemplate.Basic(
                id = "example_$smartspacerId",
                componentName = ComponentName(context!!, WeatherConditionTarget::class.java),
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
            configActivity = Intent(context, ConditionTargetConfigurationActivity::class.java)
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }
}