package complications

import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.getStringFromDataStore
import providers.AnkiWidgetProvider
import providers.dataStore

class AnkiProgressComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val ankiETA = getStringFromDataStore(context!!.dataStore, "widget_eta")
        val ankiDue = getStringFromDataStore(context!!.dataStore, "widget_due")

        Log.i("pacjodebug", "from datastore: $ankiETA / $ankiDue")

        return if (ankiETA != null && ankiDue != null) listOf(
            ComplicationTemplate.Basic(
                id = "example_$smartspacerId",
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.ankidroid
                    ),
                ),
                content = Text("Anki $ankiETA / $ankiDue"),
                onClick = TapAction(
                    intent = Intent(context!!.packageManager.getLaunchIntentForPackage(
                        AnkiWidgetProvider.PACKAGE_NAME
                    ))
                ),
                trimToFit = TrimToFit.Disabled      // TODO: can this stay? i.e. does anyone complain?
            ).create()
        ) else
            emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Anki progress",
            description = "Anki progress showing number of cards to do",
            icon = Icon.createWithResource(provideContext(), R.drawable.ankidroid),
            compatibilityState = getCompatibilityState(context, AnkiWidgetProvider.PACKAGE_NAME, "Anki isn't installed"),
            widgetProvider = "nodomain.pacjo.smartspacer.plugin.anki.widget.anki"
        )
    }
}