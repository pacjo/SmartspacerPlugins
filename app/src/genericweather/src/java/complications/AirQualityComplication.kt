package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import data.DataStoreManager.Companion.airQualityComplicationShowAlways
import data.DataStoreManager.Companion.airQualityComplicationShowThresholdKey
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.weatherDataKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.AirQualityComplicationConfigurationActivity
import utils.AirQuality
import utils.Weather
import utils.icons.AirQuality.createAqiIcon

class AirQualityComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            // get preferences
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""

            val complicationShowAlways = provideContext().dataStore.get(airQualityComplicationShowAlways) == true
            val showThreshold = provideContext().dataStore.get(airQualityComplicationShowThresholdKey) ?: AirQuality.FAIR

            val weatherData = Gson().fromJson(jsonString, Weather::class.java)

            val aqi = weatherData.airQuality.aqi

            return if (aqi > showThreshold || complicationShowAlways)
                listOf(
                    ComplicationTemplate.Basic(
                        id = "air_quality_complication_$smartspacerId",
                        icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            icon = createAqiIcon(aqi),
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
                    id = "air_quality_complication_$smartspacerId",
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
}