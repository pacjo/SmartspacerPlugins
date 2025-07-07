package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.uvIndexComplicationShowAlways
import data.DataStoreManager.Companion.uvIndexComplicationShowThresholdKey
import data.DataStoreManager.Companion.weatherDataKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.UvIndexComplicationConfigurationActivity
import utils.AirQuality
import utils.Weather
import utils.icons.UvIndex.createUvIndexIcon
import kotlin.math.roundToInt

class UvIndexComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            // get preferences
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""

            val complicationShowAlways = provideContext().dataStore.get(uvIndexComplicationShowAlways) == true
            val showThreshold = provideContext().dataStore.get(uvIndexComplicationShowThresholdKey) ?: AirQuality.FAIR

            val weatherData = Gson().fromJson(jsonString, Weather::class.java)

            val uvIndex = weatherData.uvIndex

            return if (uvIndex > showThreshold || complicationShowAlways)
                listOf(
                    ComplicationTemplate.Basic(
                        id = "uv_index_complication_$smartspacerId",
                        icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            icon = createUvIndexIcon(provideContext(), uvIndex),
                            shouldTint = false
                        ),
                        content = Text("${uvIndex.roundToInt()} UV"),
                        onClick = getPackageLaunchTapAction(provideContext(), launchPackage)
                    ).create()
                ) else emptyList()
        } else {
            // If nothing was returned above
            return listOf(
                ComplicationTemplate.Basic(
                    id = "uv_index_complication_$smartspacerId",
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
            label = "Generic UV Index",
            description = "Shows UV index information from supported apps",
            icon = Icon.createWithResource(context, R.drawable.weather_sunny_variant),
            configActivity = Intent(context, UvIndexComplicationConfigurationActivity::class.java)
        )
    }
}