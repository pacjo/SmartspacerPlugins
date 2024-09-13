package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import data.DataStoreManager.Companion.complicationTemplateKey
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.widgetDueKey
import data.DataStoreManager.Companion.widgetEtaKey
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import providers.AnkiWidgetProvider
import ui.activities.ConfigurationActivity

class AnkiProgressComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val ankiDue = provideContext().dataStore.get(widgetDueKey)
        val ankiETA = provideContext().dataStore.get(widgetEtaKey)

        val template = provideContext().dataStore.get(complicationTemplateKey) ?: "Anki {eta} / {due}"

        // for now we just show the complication if we have both fields
        // might break with card count so small eta is empty
        return if (!ankiDue.isNullOrEmpty() && !ankiETA.isNullOrEmpty()) listOf(
            ComplicationTemplate.Basic(
                id = "progress_target_$smartspacerId",
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.ankidroid
                    ),
                ),
                content = Text(
                    template
                        .replace("{due}", ankiDue)
                        .replace("{eta}", ankiETA)
                ),
                onClick = getPackageLaunchTapAction(provideContext(), AnkiWidgetProvider.PACKAGE_NAME),
                trimToFit = TrimToFit.Disabled
            ).create()
        ) else
            emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Anki progress",
            description = "Anki progress showing number of cards to do",
            icon = Icon.createWithResource(provideContext(), R.drawable.ankidroid),
            configActivity = Intent(provideContext(), ConfigurationActivity::class.java),
            compatibilityState = getCompatibilityState(provideContext(), AnkiWidgetProvider.PACKAGE_NAME, "Anki isn't installed"),
            widgetProvider = "${BuildConfig.APPLICATION_ID}.widget.anki"
        )
    }
}