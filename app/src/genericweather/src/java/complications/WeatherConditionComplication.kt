package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.annotations.LimitedNativeSupport
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import data.DataStoreManager.Companion.conditionComplicationStyleKey
import data.DataStoreManager.Companion.conditionComplicationTrimToFitKey
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.iconPackPackageNameKey
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.temperatureUnitKey
import data.DataStoreManager.Companion.weatherDataKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.ConditionComplicationConfigurationActivity
import utils.Temperature
import utils.Weather
import utils.icons.BuiltinIconProvider
import utils.icons.IconHelper.getWeatherIcon
import com.kieronquinn.app.smartspacer.sdk.model.weather.WeatherData as SmartspacerWeatherData

class WeatherConditionComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class, LimitedNativeSupport::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            // get preferences
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""
            val temperatureUnit = provideContext().dataStore.get(temperatureUnitKey) ?: "C"
            val iconPackPackageName = provideContext().dataStore.get(iconPackPackageNameKey)

            val style = provideContext().dataStore.get(conditionComplicationStyleKey) ?:"temperature"
            val trimToFit = provideContext().dataStore.get(conditionComplicationTrimToFitKey) != false

            val data = Gson().fromJson(jsonString, Weather::class.java)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "condition_complication_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        getWeatherIcon(
                            context = provideContext(),
                            iconPackPackageName = iconPackPackageName,
                            weatherData = data,
                            type = 0
                        ),
                        shouldTint = false
                    ),
                    content = Text(
                        when (style) {
                            "condition" -> data.currentCondition
                            "both" -> "${Temperature(data.currentTemp, temperatureUnit)} ${data.currentCondition}"
                            else -> Temperature(data.currentTemp, temperatureUnit).toString()
                        }
                    ),
                    onClick = getPackageLaunchTapAction(provideContext(), launchPackage),
                    trimToFit = when (trimToFit) {
                        false -> TrimToFit.Disabled
                        else -> TrimToFit.Enabled
                    }).create().apply {
                        weatherData = SmartspacerWeatherData(
                            description = data.currentCondition,
                            state = BuiltinIconProvider.getSmartspacerWeatherIcon(data, 0),
                            useCelsius = temperatureUnit != "F",
                            temperature = data.currentTemp
                    )
                }
            )
        } else {
            // If nothing was returned above
            return listOf(
                ComplicationTemplate.Basic(
                    id = "condition_complication_$smartspacerId",
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
            configActivity = Intent(context, ConditionComplicationConfigurationActivity::class.java)
        )
    }
}