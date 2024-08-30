package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.core.graphics.drawable.IconCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import nodomain.pacjo.smartspacer.plugin.utils.getStringFromDataStore
import providers.DuolingoWidgetProvider
import ui.activities.ConfigurationActivity
import java.io.File

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "progress_target_data")

class DuolingoProgressTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val context = context ?: return emptyList()
        val imageFile = File(context.filesDir, "image.png")

        val hideWhenCompleted = getBoolFromDataStore(context.dataStore, "hide_when_completed") ?: false
        val subtitle = getStringFromDataStore(context.dataStore, "widget_text")

        if (hideWhenCompleted && subtitle == "Good job!") {
            return emptyList()
        }

        return if (imageFile.exists() && subtitle != null) {
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
                onClick = getPackageLaunchTapAction(provideContext(), DuolingoWidgetProvider.PACKAGE_NAME)
            ).create().apply {
                canBeDismissed = false
            })
        } else emptyList()
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