package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import nodomain.pacjo.smartspacer.plugin.utils.getStringFromDataStore
import providers.AnkiWidgetProvider
import providers.dataStore
import ui.activities.ConfigurationActivity

class AnkiProgressComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val ankiDue = getStringFromDataStore(context!!.dataStore, "widget_due")
        val ankiETA = getStringFromDataStore(context!!.dataStore, "widget_eta")

        val template = getStringFromDataStore(context!!.dataStore, "complication_template") ?: "Anki {eta} / {due}"

        // for now we just show the complication if we have both fields
        // might break with card count so small eta is empty
        return if (!ankiDue.isNullOrEmpty() && !ankiETA.isNullOrEmpty()) listOf(
            ComplicationTemplate.Basic(
                id = "example_$smartspacerId",
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
            configActivity = Intent(context, ConfigurationActivity::class.java),
            compatibilityState = getCompatibilityState(context, AnkiWidgetProvider.PACKAGE_NAME, "Anki isn't installed"),
            widgetProvider = "nodomain.pacjo.smartspacer.plugin.anki.widget.anki"
        )
    }
}