package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.IconCompat
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import providers.DuolingoWidgetProvider
import ui.activities.ConfigurationActivity
import java.io.File

class DuolingoProgressTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        isFirstRun(context!!)

        val imageFile = File(context?.filesDir, "image.png")
        val dataFile = File(context?.filesDir, "data.json")

        val jsonString = dataFile.readText()
        val jsonObject = JSONObject(jsonString)

        // get data object (data key in json)
        val data = jsonObject.getJSONObject("data")

        val preferences = jsonObject.getJSONObject("preferences")
        val hideWhenCompleted = preferences.optBoolean("hide_when_completed", false)

        val subtitle = when {
            data.optString("streakSubtitle") != "" -> data.optString("streakSubtitle")
            data.optString("encouragingSubtitle") != "" -> data.optString("encouragingSubtitle")
            data.optString("otherModeText") != "" -> data.optString("otherModeText")
//            data.optString("negativeStreakSubtitle") != "" -> data.optString("negativeStreakSubtitle")
            else -> "Good job!"     // there's no text when everything is good
        }

        return if (hideWhenCompleted && subtitle == "Good job!") {
            emptyList()
        } else {
            if (imageFile.exists() && dataFile.exists()) {
                listOf(TargetTemplate.Image(
                    context = provideContext(),
                    id = "example_$smartspacerId",
                    componentName = ComponentName(
                        provideContext(),
                        DuolingoProgressTarget::class.java
                    ),
                    title = Text("Duolingo"),
                    subtitle = Text(subtitle),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            provideContext(),
                            R.drawable.duolingo
                        ),
                    ),
                    image = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithBitmap(
                            BitmapFactory.decodeFile(imageFile.absolutePath),
                        ).toIcon(context),
                        shouldTint = false
                    ),
                    onClick = TapAction(
                        intent = Intent(
                            context!!.packageManager.getLaunchIntentForPackage(
                                DuolingoWidgetProvider.PACKAGE_NAME
                            )
                        )
                    )
                ).create().apply {
                    canBeDismissed = false
                })
            } else emptyList()
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Duolingo progress",
            description = "Duolingo progress and motivational message",
            icon = Icon.createWithResource(provideContext(), R.drawable.duolingo),
            configActivity = Intent(context, ConfigurationActivity::class.java),
            compatibilityState = getCompatibilityState(context, DuolingoWidgetProvider.PACKAGE_NAME, "Duolingo isn't installed"),
            widgetProvider = "nodomain.pacjo.smartspacer.plugin.duolingo.widget.duolingo"
        )
    }
    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }
}