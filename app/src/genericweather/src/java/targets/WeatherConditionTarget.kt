package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toIcon
import androidx.core.graphics.scale
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.CarouselTemplateData
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.DataStoreManager.Companion.conditionTargetCarouselContentKey
import data.DataStoreManager.Companion.conditionTargetDataPointsKey
import data.DataStoreManager.Companion.conditionTargetStyleKey
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.iconPackPackageNameKey
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.temperatureUnitKey
import data.DataStoreManager.Companion.weatherDataKey
import data.ForecastTargetDataType
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.ConditionTargetConfigurationActivity
import utils.Temperature
import utils.Weather
import utils.icons.AirQuality.createAqiIcon
import utils.icons.BreezyIconProvider
import utils.icons.BuiltinIconProvider
import utils.icons.IconHelper.getWeatherIcon
import utils.icons.IconPackInfo
import java.time.Instant
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

class WeatherConditionTarget: SmartspacerTargetProvider() {

    private fun createCarouselItemList(
        iconPack: IconPackInfo?,
        iconProvider: BreezyIconProvider,
        weatherData: Weather,
        numberOfDataPoints: Int,
        targetTemperatureUnit: String,
        targetType: ForecastTargetDataType
    ): List<CarouselTemplateData.CarouselItem> {
        val carouselItemList = mutableListOf<CarouselTemplateData.CarouselItem>()

        for (index in 0..<numberOfDataPoints) {
            val dataPoint: Pair<String, String> = when (targetType) {
                // triple: text / timestamp / icon
                ForecastTargetDataType.TEMPERATURE_HOURLY ->
                    Pair(
                        first = Temperature(weatherData.hourly[index].temp, targetTemperatureUnit).toString(),
                        second = Time(provideContext(), weatherData.hourly[index].timestamp).getEventTime()
                    )

                ForecastTargetDataType.TEMPERATURE_DAILY ->
                    Pair(
                        // TODO: maybe we should show min temp too?
                        first = Temperature(weatherData.forecasts[index].maxTemp, targetTemperatureUnit).toString(),
                        second = Time(provideContext(), Instant.now().truncatedTo(ChronoUnit.DAYS).plus(index.toLong(), ChronoUnit.DAYS).getLong(ChronoField.INSTANT_SECONDS)).getEventDate( "EEE")
                    )

                ForecastTargetDataType.AIR_QUALITY_DAILY ->
                    Pair(
                        first = weatherData.forecasts[index].airQuality!!.aqi.toString(),
                        second = Time(provideContext(), Instant.now().truncatedTo(ChronoUnit.DAYS).plus(index.toLong(), ChronoUnit.DAYS).getLong(ChronoField.INSTANT_SECONDS)).getEventDate( "EEE")
                    )
            }

            val carouselItem = CarouselTemplateData.CarouselItem(
                upperText = Text(dataPoint.first),
                lowerText = Text(" ${dataPoint.second} "),
                image = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    when (targetType) {
                        // TODO: figure out scaling issue
                        // also icon pack icons look pixelated
                        // when calling .toBitmap() in IconHelper we can pass width and height, how about doing that?
                        ForecastTargetDataType.TEMPERATURE_HOURLY, ForecastTargetDataType.TEMPERATURE_DAILY -> {
                            (
                                if (iconPack != null)
                                    iconProvider.getWeatherIcon(
                                        iconPack = iconPack,
                                        data = weatherData,
                                        type = targetType.ordinal + 1,      // hacky, but works (until it doesn't)
                                        index = index
                                    ).toBitmap().scale(
                                        (12 * resources.displayMetrics.density).toInt(),
                                        (12 * resources.displayMetrics.density).toInt()
                                    )
                                else
                                    ContextCompat.getDrawable(
                                        provideContext(),
                                        BuiltinIconProvider.getWeatherIcon(
                                            context = provideContext(),
                                            data = weatherData,
                                            type = targetType.ordinal + 1,      // hacky, but works (until it doesn't)
                                            index = index
                                        )
                                    )!!.toBitmap().scale(
                                        (24 * resources.displayMetrics.density).toInt(),
                                        (24 * resources.displayMetrics.density).toInt()
                                    )
                                ).toIcon()
                        }

                        ForecastTargetDataType.AIR_QUALITY_DAILY -> createAqiIcon(weatherData.forecasts[index].airQuality!!.aqi)
                    },
                    shouldTint = false
                ),
                tapAction = null
            )

            carouselItemList.add(carouselItem)
        }

        return carouselItemList
    }

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            // get preferences
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""
            val temperatureUnit = provideContext().dataStore.get(temperatureUnitKey) ?: "C"
            val iconPackPackageName = provideContext().dataStore.get(iconPackPackageNameKey)

            val targetStyle = provideContext().dataStore.get(conditionTargetStyleKey) ?: "both"
            val carouselContentType = ForecastTargetDataType.entries[provideContext().dataStore.get(conditionTargetCarouselContentKey) ?: 0]
            val dataPoints = provideContext().dataStore.get(conditionTargetDataPointsKey) ?: 4

            // rewrite this icon code
            val iconProvider = BreezyIconProvider(provideContext())
            var iconPack: IconPackInfo? = null
            if (iconPackPackageName != null)
                iconPack = iconProvider.getIconPackByPackageName(iconPackPackageName)

            val weatherData = Gson().fromJson(jsonString, Weather::class.java)

            val currentTemperature = weatherData.currentTemp
            val location = weatherData.location
            val currentCondition = weatherData.currentCondition

            if (dataPoints > 0) {
                return listOf(TargetTemplate.Carousel(
                    id = "condition_target_$smartspacerId",
                    componentName = ComponentName(context!!, WeatherConditionTarget::class.java),
                    title = Text(location),
                    subtitle = Text(
                        when (targetStyle) {
                            "condition" -> currentCondition
                            "both" -> "${Temperature(currentTemperature, temperatureUnit)}, $currentCondition"

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
                    items = createCarouselItemList(
                        iconPack = iconPack,
                        iconProvider = iconProvider,
                        weatherData = weatherData,
                        numberOfDataPoints = dataPoints,
                        targetTemperatureUnit = temperatureUnit,
                        targetType = carouselContentType
                    ),
                    onClick = getPackageLaunchTapAction(provideContext(), launchPackage),
                    onCarouselClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                ).create().apply {
                    canBeDismissed = false
                })
            } else {
                return listOf(TargetTemplate.Basic(
                    id = "condition_target_$smartspacerId",
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
                id = "condition_target_$smartspacerId",
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