package ui.composables

import android.content.Context
import androidx.compose.runtime.Composable
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.GenericWeatherComplication
import data.PreferencesKeys
import data.dataStore
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceInput
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceMenu
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.GenericWeatherTarget
import targets.WeatherForecastTarget
import utils.icons.BreezyIconProvider

/**
 * A couple of composables for settings shared between targets and complications
 */
@Composable
fun GeneralSettings(context: Context) {
    val iconProvider = BreezyIconProvider(context)
    val iconPacks = iconProvider.getInstalledIconPacks()

    val launchPackage = context.dataStore.get(PreferencesKeys.LAUNCH_PACKAGE) ?: ""

    PreferenceHeading("General settings")

    PreferenceMenu(
        icon = R.drawable.package_variant,
        title = "Icon pack",
        description = "Select icon pack to use",
        onItemChange = { value ->
            context.dataStore.save(PreferencesKeys.ICON_PACK_PACKAGE_NAME, value)

            notifyAllProviders(context)
        },
        items =
            iconPacks
                .filter { iconPack ->
                    iconPack.hasWeatherIcons
                }
                .map { iconPack ->
                Pair(iconPack.name, iconPack.packageName)
            }.plus(
                Pair("Default", null)
            )
    )

    PreferenceMenu(
        icon = R.drawable.thermometer,
        title = "Temperature unit",
        description = "Select preferred unit",
        onItemChange = { value ->
            context.dataStore.save(PreferencesKeys.TEMPERATURE_UNIT, value)

            notifyAllProviders(context)
        },
        items = listOf(
            Pair("Kelvin", "K"),
            Pair("Celsius", "C"),
            Pair("Fahrenheit", "F")
        )
    )

    PreferenceInput(
        icon = R.drawable.package_variant,
        title = "Launch package",
        description = "Select package name of an app to open when target / complication is clicked",
        onTextChange = { value ->
            context.dataStore.save(PreferencesKeys.LAUNCH_PACKAGE, value)

            notifyAllProviders(context)
        },
        dialogText = "Enter package name",
        defaultText = launchPackage
    )
}

private fun notifyAllProviders(context: Context) {
    SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
    SmartspacerTargetProvider.notifyChange(context, WeatherForecastTarget::class.java)
    SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
}