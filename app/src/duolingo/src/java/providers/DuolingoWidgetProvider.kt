package providers

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerWidgetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.findViewByIdentifier
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import targets.DuolingoProgressTarget
import java.io.File
import java.io.FileOutputStream


class DuolingoWidgetProvider: SmartspacerWidgetProvider() {

    companion object {
        private const val PACKAGE_NAME = "com.duolingo"
        private const val PROVIDER_CLASS = "com.duolingo.streak.streakWidget.StreakWidgetProvider"

        fun getProvider(context: Context): AppWidgetProviderInfo? {
            val manager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
            return manager.installedProviders.firstOrNull {
                it.provider.packageName == PACKAGE_NAME && it.provider.className == PROVIDER_CLASS
            }
        }

        // The following code needs some explanation
        //   - the view object we get is roughly square and we need it to be rectangular with ~16:9 ratio
        //   - the background is a gradient
        //   - no matter what I do, I can't get the widget in other proportions
        // so we (and by that I mean "I"):
        //   1. make a canvas that's (16/9)*height of the original view (since at a glance tries to fit image vertically-first)
        //   2. get background drawable and resize it to the new size
        //   3. remove background from original view
        //   4. draw both background and background-less views onto canvas
        //   5. save the result so it can be used later
        // sound simple, looks nice, took way to long

        fun saveViewToFile(view: View, file: File) {
//            view.dumpToLog("Views")

            // without this we get empty image
            view.measure(0, 0)  // those don't matter
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            // this is our gradient background
            val backgroundView = view.findViewByIdentifier<ImageView>("com.duolingo:id/background")
            backgroundView?.layout(
                0,
                0,
                (view.measuredHeight * (16/9F)).toInt(),
                view.measuredHeight
            )

            // remove background from widget view
            (view as ViewGroup).removeView(view.findViewByIdentifier<ImageView>("com.duolingo:id/background"))

            val extendedBitmap = Bitmap.createBitmap(
                (view.measuredHeight * (16/9F)).toInt(),
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )

            val extendedCanvas = Canvas(extendedBitmap)

            // background
            extendedCanvas.save()
            backgroundView?.draw(extendedCanvas)

            // foreground (original view without background)
            extendedCanvas.translate((((view.measuredHeight * (16/9F)) - view.measuredWidth))/2, 0F)
            view.draw(extendedCanvas)        // paint the parent with background removed

            // TODO: we could also remove the text

            // Save the bitmap to a file
            FileOutputStream(file).use { out ->
                extendedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
        }
    }

    override fun onWidgetChanged(smartspacerId: String, remoteViews: RemoteViews?) {
        isFirstRun(provideContext())
        val dataFile = File(context?.filesDir, "data.json")
        val imageFile = File(context?.filesDir, "image.png")     // TODO: don't hardcode

        // Load the RemoteViews into regular Views
        val view = remoteViews?.load() ?: return

        // extract messages from target
        val ids = listOf("streakSubtitle", "otherModeText", "negativeStreakSubtitle", "encouragingSubtitle")
        for (id in ids) {
            Log.i("pacjodebug", "$id: ${view.findViewByIdentifier<TextView>("com.duolingo:id/$id")?.text}")
            // TODO: write generic save

            // Read JSON
            val jsonObject = JSONObject(dataFile.readText())
            val dataObject = jsonObject.getJSONObject("data")

            dataObject.put(id, view.findViewByIdentifier<TextView>("com.duolingo:id/$id")?.text)

            dataFile.writeText(jsonObject.toString())
        }

        saveViewToFile(view, imageFile)

        // Notify target about new data
        SmartspacerTargetProvider.notifyChange(provideContext(), DuolingoProgressTarget::class.java)
    }

    override fun getAppWidgetProviderInfo(smartspacerId: String): AppWidgetProviderInfo? {
        return getProvider(provideContext())
    }

    override fun getConfig(smartspacerId: String): Config {
        // don't specify size, as we modify it either way
        return Config()
    }
}