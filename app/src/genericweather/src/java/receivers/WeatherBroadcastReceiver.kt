package receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.AirQualityComplication
import complications.SunTimesComplication
import complications.WeatherConditionComplication
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.weatherDataKey
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.WeatherConditionTarget
import targets.WeatherForecastTarget

class WeatherBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // save data to file
        val weatherData = intent.getStringExtra("WeatherJson")

        if (weatherData != null)
            context.dataStore.save(weatherDataKey, weatherData)

        SmartspacerTargetProvider.notifyChange(context, WeatherConditionTarget::class.java)
        SmartspacerComplicationProvider.notifyChange(context, WeatherConditionComplication::class.java)
        SmartspacerComplicationProvider.notifyChange(context, SunTimesComplication::class.java)
        SmartspacerComplicationProvider.notifyChange(context, AirQualityComplication::class.java)
    }
}