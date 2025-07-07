package utils.icons

import android.content.Context
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.toBitmap
import utils.Weather

object IconHelper {
    fun getWeatherIcon(
        context: Context,
        iconPackPackageName: String? = null,
        weatherData: Weather,
        type: Int,
        index: Int = 0
    ): Icon {
        if (iconPackPackageName != null) {
            val iconProvider = BreezyIconProvider(context)
            val iconPack = iconProvider.getIconPackByPackageName(iconPackPackageName)

            if (iconPack != null)
                return Icon.createWithBitmap(
                    iconProvider.getWeatherIcon(
                        iconPack = iconPack,
                        data = weatherData,
                        type = type,
                        index = index
                    ).toBitmap()
                )
        }

        // default to builtin icons
        return Icon.createWithResource(
            context,
            BuiltinIconProvider.getWeatherIcon(
                context = context,
                data = weatherData,
                type = type,
                index = index
            )
        )
    }
}