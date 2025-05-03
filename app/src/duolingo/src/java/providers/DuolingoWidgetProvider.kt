package providers

import android.appwidget.AppWidgetProviderInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.graphics.createBitmap
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerWidgetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.findViewByIdentifier
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.widgetSubtitleKey
import nodomain.pacjo.smartspacer.plugin.utils.getProvider
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.DuolingoProgressTarget
import java.io.File
import java.io.FileOutputStream

class DuolingoWidgetProvider: SmartspacerWidgetProvider() {

    companion object {
        const val PACKAGE_NAME = "com.duolingo"
        private const val PROVIDER_CLASS = "com.duolingo.streak.streakWidget.StreakWidgetProvider"

        fun saveWidgetViewToFile(view: View, file: File) {
            // get some starting size
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            // recalculate size in 16x9 ratio
            // (recalculated from width since at the time of writing duolingo gives us a 3px high widget)
            val recalculatedWidth = view.width
            val recalculatedHeight = (view.width * (9f/16f)).toInt()

            val extendedBitmap = createBitmap(recalculatedWidth, recalculatedHeight)

            val extendedCanvas = Canvas(extendedBitmap)

            view.run {
                // resize
                measure(
                    View.MeasureSpec.makeMeasureSpec(recalculatedWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(recalculatedHeight, View.MeasureSpec.EXACTLY)
                )
                layout(0, 0, recalculatedWidth, recalculatedHeight)

                // and draw
                draw(extendedCanvas)
            }

            // Save the bitmap to a file
            FileOutputStream(file).use { out ->
                extendedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
        }
    }

    override fun onWidgetChanged(smartspacerId: String, remoteViews: RemoteViews?) {
        val imageFile = File(context?.filesDir, "image.png")

        // Load the RemoteViews into regular Views
        val view = remoteViews?.load() ?: return

        // extract messages from target (and remove them from view)
        val subtitleIDs = listOf(
            "streakSubtitle",
            "otherModeText",
            "encouragingSubtitle"
        )

        val subtitles = mutableListOf<String?>()

        for (subtitleID in subtitleIDs) {
            subtitles.add(view.findViewByIdentifier<TextView>("$PACKAGE_NAME:id/$subtitleID")?.text as String?)

            // remove element from widget
            view.findViewByIdentifier<TextView>("$PACKAGE_NAME:id/$subtitleID")?.text  = ""
        }

        val subtitle = subtitles.firstOrNull { it?.isNotBlank() == true } ?: "Good job!"
        provideContext().dataStore.save(widgetSubtitleKey, subtitle)

        saveWidgetViewToFile(view, imageFile)

        // Notify target about new data
        SmartspacerTargetProvider.notifyChange(provideContext(), DuolingoProgressTarget::class.java)
    }

    override fun getAppWidgetProviderInfo(smartspacerId: String): AppWidgetProviderInfo? {
        return getProvider(provideContext(), PACKAGE_NAME, PROVIDER_CLASS)
    }

    override fun getConfig(smartspacerId: String): Config {
        // don't specify size, as we modify it either way
        return Config()
    }
}