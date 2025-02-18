package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.DataStoreManager.Companion.conditionTargetCarouselContentKey
import data.DataStoreManager.Companion.conditionTargetDataPointsKey
import data.DataStoreManager.Companion.conditionTargetStyleKey
import data.DataStoreManager.Companion.dataStore
import data.ForecastTargetDataType
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceMenu
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSlider
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.WeatherConditionTarget
import ui.composables.GeneralSettings

class ConditionTargetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val dataPoints = context.dataStore.get(conditionTargetDataPointsKey) ?: 4

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    GeneralSettings(context)

                    PreferenceHeading("Weather target")

                    PreferenceMenu(
                        icon = R.drawable.palette_outline,
                        title = "Style",
                        description = "Select target style",
                        onItemChange = { value ->
                            context.dataStore.save(conditionTargetStyleKey, value)

                            SmartspacerTargetProvider.notifyChange(context, WeatherConditionTarget::class.java)
                        },
                        items = listOf(
                            Pair("Temperature only", "temperature"),
                            Pair("Condition only", "condition"),
                            Pair("Temperature and condition", "both")
                        )
                    )

                    PreferenceMenu(
                        icon = R.drawable.palette_outline,
                        title = "Carousel content",
                        description = "Data shown in carousel",
                        onItemChange = { value ->
                            context.dataStore.save(conditionTargetCarouselContentKey, value.ordinal)

                            SmartspacerTargetProvider.notifyChange(context, WeatherConditionTarget::class.java)
                        },
                        items = listOf(
                            Pair("Hourly temperature", ForecastTargetDataType.TEMPERATURE_HOURLY),
                            Pair("Daily temperature", ForecastTargetDataType.TEMPERATURE_DAILY),
                            Pair("Daily air quality", ForecastTargetDataType.AIR_QUALITY_DAILY)
                        )
                    )

                    PreferenceSlider(
                        icon = R.drawable.calendar_range_outline,
                        title = "Forecast points to show",
                        description = "Select number of visible forecast days/hours",
                        onSliderChange = { value ->
                            context.dataStore.save(conditionTargetDataPointsKey, value)

                            SmartspacerTargetProvider.notifyChange(context, WeatherConditionTarget::class.java)
                        },
                        range = (0..4),
                        defaultPosition = dataPoints
                    )
                }
            }
        }
    }
}