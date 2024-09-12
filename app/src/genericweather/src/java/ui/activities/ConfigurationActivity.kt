package ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.GenericAirQualityComplication
import complications.GenericSunTimesComplication
import complications.GenericWeatherComplication
import data.PreferencesKeys
import data.dataStore
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceInput
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceMenu
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSlider
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import nodomain.pacjo.smartspacer.plugin.utils.save
import nodomain.pacjo.smartspacer.plugin.utils.savePreference
import org.json.JSONObject
import targets.GenericWeatherTarget
import targets.WeatherForecastTarget
import utils.icons.BreezyIconProvider
import java.io.File

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            val iconProvider = BreezyIconProvider(this)
            val iconPacks = iconProvider.getInstalledIconPacks()

            // get number of forecast points (as we need it to show the default)
            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")
            val dataPoints = preferences.optInt("target_points_visible", 4)
            val launchPackage = preferences.optString("launch_package", "")

            val conditionComplicationTrimToFit = preferences.optBoolean("condition_complication_trim_to_fit", true)
            val sunTimesComplicationTrimToFit = preferences.optBoolean("suntimes_complication_trim_to_fit", true)
            val airQualityComplicationShowAlways = preferences.optBoolean("air_quality_complication_show_always", false)

            PluginTheme {
                PreferenceLayout("Generic Weather") {

                    PreferenceMenu (
                    PreferenceMenu(
                        icon = R.drawable.alert_circle,      // TODO: change
                        title = "Icon pack",
                        description = "Select icon pack to use",
                        onItemChange = {
                            value -> context.dataStore.save(PreferencesKeys.ICON_PACK_PACKAGE_NAME, value as String?)

                            SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                            SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                        },
                        items =     // TODO: filter for weatherIcon support
                            iconPacks.map { iconPack ->
                                Pair(iconPack.name, iconPack.packageName)
                            }.plus(
                                Pair("Default", null)
                            )
                    )

                    PreferenceMenu(
                        icon = R.drawable.thermometer,
                        title = "Temperature Unit",
                        description = "Select preferred unit",
                        onItemChange = {
                            value -> savePreference(context,"unit", value!!)
                            SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                            // TODO: notify the rest too
                        },
                        items = listOf(
                            Pair("Kelvin", "K"),
                            Pair("Celsius", "C"),
                            Pair("Fahrenheit", "F")
                        )
                    )

                    PreferenceInput(
                        icon = R.drawable.package_variant,
                        title = "Launch Package",
                        description = "Select package name of an app to open when target / complication is clicked",
                        onTextChange = {
                            value -> savePreference(context,"launch_package", value)
                            SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            // TODO: notify the rest too
                        },
                        dialogText = "Enter package name",
                        defaultText = launchPackage
                    )

                    PreferenceHeading("Weather target")

                    PreferenceMenu(
                        icon = R.drawable.palette_outline,
                        title = "Style",
                        description = "Select target style",
                        onItemChange = {
                            value -> savePreference(context,"target_style", value!!)
                            SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                        },
                        items = listOf(
                            Pair("Temperature only", "temperature"),
                            Pair("Condition only", "condition"),
                            Pair("Temperature and condition", "both")
                        )
                    )

                    PreferenceSlider(
                        icon = R.drawable.calendar_range_outline,
                        title = "Forecast points to show",
                        description = "Select number of visible forecast days/hours",
                        onSliderChange = {
                            value -> savePreference(context,"target_points_visible", value)
                            Log.i("pacjodebug", "callback, value: $value")
                            SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                        },
                        range = (0..4),
                        defaultPosition = dataPoints.toFloat()
                    )

                    PreferenceHeading("Weather complication")

                    PreferenceMenu(
                        icon = R.drawable.palette_outline,
                        title = "Style",
                        description = "Select complication style",
                        onItemChange = {
                            value -> savePreference(context,"condition_complication_style", value!!)
                            SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                        },
                        items = listOf(
                            Pair("Temperature only", "temperature"),
                            Pair("Condition only", "condition"),
                            Pair("Temperature and condition", "both")
                        )
                    )

                    PreferenceSwitch(
                        icon = R.drawable.content_cut,
                        title = "Complication text trimming",
                        description = "Disable this if text is getting cut off. May cause unexpected results",
                        onCheckedChange = {
                            value -> savePreference(context,"condition_complication_trim_to_fit", value)
                            SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                        },
                        checked = conditionComplicationTrimToFit
                    )

                    PreferenceHeading("Sun times complication")

                    PreferenceSwitch(
                        icon = R.drawable.content_cut,
                        title = "Complication text trimming",
                        description = "Disable this if text is getting cut off. May cause unexpected results",
                        onCheckedChange = {
                            value -> savePreference(context,"suntimes_complication_trim_to_fit", value)
                            SmartspacerComplicationProvider.notifyChange(context, GenericSunTimesComplication::class.java)
                        },
                        checked = sunTimesComplicationTrimToFit
                    )

                    PreferenceHeading("Air quality complication")

                    PreferenceSwitch(
                        icon = R.drawable.eye_off,
                        title = "Show always",
                        description = "Enable this to show complication regardless of current AQI value",
                        onCheckedChange = {
                            value -> savePreference(context,"air_quality_complication_show_always", value)
                            SmartspacerComplicationProvider.notifyChange(context, GenericAirQualityComplication::class.java)
                        },
                        checked = airQualityComplicationShowAlways
                    )
                }
            }
        }
    }
}

