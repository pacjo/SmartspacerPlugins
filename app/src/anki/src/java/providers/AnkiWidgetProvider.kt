package providers

import android.appwidget.AppWidgetProviderInfo
import android.widget.RemoteViews
import android.widget.TextView
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerWidgetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.findViewByIdentifier
import complications.AnkiProgressComplication
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.widgetDueKey
import data.DataStoreManager.Companion.widgetEtaKey
import nodomain.pacjo.smartspacer.plugin.utils.getProvider
import nodomain.pacjo.smartspacer.plugin.utils.save

class AnkiWidgetProvider: SmartspacerWidgetProvider() {

    companion object {
        const val PACKAGE_NAME = "com.ichi2.anki"
        private const val PROVIDER_CLASS = "com.ichi2.widget.AnkiDroidWidgetSmall"
    }

    override fun onWidgetChanged(smartspacerId: String, remoteViews: RemoteViews?) {
        val view = remoteViews?.load() ?: return

        val ankiDue = view.findViewByIdentifier<TextView>("$PACKAGE_NAME:id/widget_due")?.text
        val ankiETA = view.findViewByIdentifier<TextView>("$PACKAGE_NAME:id/widget_eta")?.text

        provideContext().dataStore.save(widgetDueKey, ankiDue.toString())
        provideContext().dataStore.save(widgetEtaKey, ankiETA.toString())

        // Notify complication about new data
        SmartspacerComplicationProvider.notifyChange(provideContext(), AnkiProgressComplication::class.java)
    }

    override fun getAppWidgetProviderInfo(smartspacerId: String): AppWidgetProviderInfo? {
        return getProvider(provideContext(), PACKAGE_NAME, PROVIDER_CLASS)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config()
    }
}