package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.icu.util.Calendar
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.iconPackPackageNameKey
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.temperatureUnitKey
import data.DataStoreManager.Companion.weatherDataKey
import data.TargetMode
import data.getDismissDateKey
import data.getDismissedKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time.Companion.getCurrentDate
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.ForecastTargetConfigurationActivity
import utils.Temperature
import utils.WeatherData
import utils.icons.IconHelper.getWeatherIcon

private fun saveDismissalState(context: Context, dismissed: Boolean, targetMode: TargetMode) {
    runBlocking {
        context.dataStore.edit { preferences ->
            preferences[getDismissedKey(targetMode)] = dismissed
            preferences[getDismissDateKey(targetMode)] = getCurrentDate()
        }
    }
}

private fun getDismissalState(context: Context, targetMode: TargetMode): Pair<Boolean, String?> {
    return runBlocking {
        val preferences = context.dataStore.data.first()
        val dismissed = preferences[getDismissedKey(targetMode)] == true
        val dismissDate = preferences[getDismissDateKey(targetMode)]

        dismissed to dismissDate
    }
}

private fun getPrecipitationWarning(weatherData: WeatherData): String? {
    val currentTime = System.currentTimeMillis()
    val twoHoursInMillis = 2 * 60 * 60 * 1000

    val nextTwoHours = weatherData.hourly.filter { it.timestamp * 1000 in currentTime..(currentTime + twoHoursInMillis) }
    val rainForecast = nextTwoHours.find { it.conditionCode in 200..699 }

    return if (rainForecast != null && weatherData.currentConditionCode !in 200..699) {
        val precipitationType =  when (rainForecast.conditionCode) {
            in 200..299 -> "thunderstorms"
            in 300..599 -> "rain"
            in 600..699 -> "snow"

            else -> throw IllegalArgumentException("Condition code out of range")
        }

        val precipitationTime =
            if (rainForecast.timestamp * 1000 - currentTime < 60 * 60 * 1000) {
                "less than an hour"
            } else {
                val numberOfHours = ((rainForecast.timestamp * 1000 - currentTime) / (60 * 60 * 1000)).toInt()

                if (numberOfHours == 1)
                    "$numberOfHours hour"
                else
                    "$numberOfHours hours"
            }

        return "Expected $precipitationType in $precipitationTime"
    } else {
        null
    }
}

class WeatherForecastTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentDate = getCurrentDate()

            val targets = mutableListOf<SmartspaceTarget>()

            val todayForecastState = getDismissalState(provideContext(), TargetMode.FORECAST_TODAY)
            val tomorrowForecastState = getDismissalState(provideContext(), TargetMode.FORECAST_TOMORROW)
            val expectedRainState = getDismissalState(provideContext(), TargetMode.EXPECTED_PRECIPITATION)

            // get preferences
            val temperatureUnit = provideContext().dataStore.get(temperatureUnitKey) ?: "C"
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""
            val iconPackPackageName = provideContext().dataStore.get(iconPackPackageNameKey)

            val gson = Gson()
            val weatherData = gson.fromJson(jsonString, WeatherData::class.java)

            // TODO: time should be configurable
            if (hour in 9..10 && (!todayForecastState.first || todayForecastState.second != currentDate)) {
                 targets.add(
                    TargetTemplate.Basic(
                        id = "weather_today_target_$smartspacerId",
                        componentName = ComponentName(context!!, WeatherForecastTarget::class.java),
                        title = Text("Today ${Temperature(weatherData.todayMaxTemp, temperatureUnit)} / ${Temperature(weatherData.todayMinTemp, temperatureUnit)}"),
                        subtitle = Text(weatherData.currentCondition),
                        icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            getWeatherIcon(
                                context = provideContext(),
                                iconPackPackageName = iconPackPackageName,
                                weatherData = weatherData,
                                type = 0
                            ),
                            shouldTint = false
                        ),
                        onClick = getPackageLaunchTapAction(provideContext(), launchPackage),
                        // prevent second complication (it should be possible to instruct smartspacer to make the option activated by default)
                        subComplication = ComplicationTemplate.blank().create()
                    ).create()
                )
            }

            // TODO: time should be configurable
            if (hour in 21..22 && (!tomorrowForecastState.first || tomorrowForecastState.second != currentDate)) {
                val temperatureDiff = Temperature(weatherData.forecasts[0].maxTemp, weatherData.todayMaxTemp, temperatureUnit)
                val diffPhrase = when {
                    // temperature diff can be negative, so we take away the minus just in case,
                    // TODO: there's for sure a better way to write this
                    temperatureDiff.temperature!! < 0 -> "${temperatureDiff.toString().replace("-", "")} colder than"
                    temperatureDiff.temperature!! > 0 -> "${temperatureDiff.toString().replace("-", "")} warmer than"

                    else -> "the same as"
                }

                targets.add(
                    TargetTemplate.Basic(
                        id = "weather_tomorrow_target_$smartspacerId",
                        componentName = ComponentName(context!!, WeatherForecastTarget::class.java),
                        title = Text("Tomorrow $diffPhrase today"),
                        subtitle = null,
                        icon = null,
                        onClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                    ).create().apply {
                        canTakeTwoComplications = true
                    }
                )
            }

            val precipitationWarning = getPrecipitationWarning(weatherData)
            if (precipitationWarning != null && (!expectedRainState.first || expectedRainState.second != currentDate)) {
                targets.add(
                    TargetTemplate.Basic(
                        id = "precipitation_target_$smartspacerId",
                        componentName = ComponentName(provideContext(), WeatherForecastTarget::class.java),
                        title = Text(precipitationWarning),
                        subtitle = null,
                        icon = null,
                        onClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                    ).create().apply {
                        canTakeTwoComplications = true
                    }
                )
            }

            return targets
        }

        return emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Contextual weather information",
            description = "Shows weather messages based on current conditions",
            icon = Icon.createWithResource(context, R.drawable.weather_partly_rainy),
            configActivity = Intent(context, ForecastTargetConfigurationActivity::class.java)
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        saveDismissalState(
            context = provideContext(),
            dismissed = true,
            targetMode = when {
                targetId.startsWith("weather_today_target_") -> TargetMode.FORECAST_TODAY
                targetId.startsWith("weather_tomorrow_target_") -> TargetMode.FORECAST_TOMORROW
                targetId.startsWith("precipitation_target_") -> TargetMode.EXPECTED_PRECIPITATION

                else -> return false        // this should not happen
            }
        )

        notifyChange(smartspacerId)

        return true
    }
}