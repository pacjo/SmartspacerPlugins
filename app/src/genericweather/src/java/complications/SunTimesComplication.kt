package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.launchPackageKey
import data.DataStoreManager.Companion.sunTimesComplicationTrimToFitKey
import data.DataStoreManager.Companion.weatherDataKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.Time
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import ui.activities.SunTimesComplicationConfigurationActivity
import utils.WeatherData
import kotlin.math.min

class SunTimesComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val jsonString = provideContext().dataStore.get(weatherDataKey)

        if (jsonString != null) {
            // get preferences
            val launchPackage = provideContext().dataStore.get(launchPackageKey) ?: ""

            val trimToFit = provideContext().dataStore.get(sunTimesComplicationTrimToFitKey) != false

            val weatherData = Gson().fromJson(jsonString, WeatherData::class.java)

            val nextSunrise = when (System.currentTimeMillis() < weatherData.sunRise * 1000L) {
                true -> weatherData.sunRise                 // if we're still before today's sunrise
                else -> weatherData.forecasts[0].sunRise
            }.toLong()

            val nextSunset = when (System.currentTimeMillis() < weatherData.sunSet * 1000L) {
                true -> weatherData.sunSet                 // if we're still before today's sunset
                else -> weatherData.forecasts[0].sunSet
            }.toLong()

            // so, we have next sunrise and sunset
            // we'll always show next event (relative to the current time)
            // so it'll be min(nextSunrise, nextSunset)

            val nextEvent = min(nextSunrise, nextSunset)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "suntimes_complication_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            context,
                            when (nextEvent == nextSunrise) {
                                true -> R.drawable.ic_sunrise
                                else -> R.drawable.ic_sunset
                            }
                        )
                    ),
                    content = Text(Time(provideContext(), nextEvent).getEventTime()),
                    onClick = getPackageLaunchTapAction(provideContext(), launchPackage),
                    trimToFit = when (trimToFit) {
                        false -> TrimToFit.Disabled
                        else -> TrimToFit.Enabled
                    }
                ).create()
            )
        } else {
            // If nothing was returned above
            return listOf(
                ComplicationTemplate.Basic(
                    id = "suntimes_complication_$smartspacerId",
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
            label = "Generic sun times",
            description = "Shows sunrise / sunset information from supported apps",
            icon = Icon.createWithResource(context, R.drawable.ic_sunrise),
            configActivity = Intent(context, SunTimesComplicationConfigurationActivity::class.java)
        )
    }

}