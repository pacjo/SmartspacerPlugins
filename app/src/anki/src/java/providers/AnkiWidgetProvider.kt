package providers

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.widget.RemoteViews
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerWidgetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.findViewByIdentifier
import complications.AnkiProgressComplication
import nodomain.pacjo.smartspacer.plugin.utils.getProvider
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "anki_widget_data")

class AnkiWidgetProvider: SmartspacerWidgetProvider() {

    companion object {
        const val PACKAGE_NAME = "com.ichi2.anki"
        private const val PROVIDER_CLASS = "com.ichi2.widget.AnkiDroidWidgetSmall"
    }

    override fun onWidgetChanged(smartspacerId: String, remoteViews: RemoteViews?) {
        val view = remoteViews?.load() ?: return

        val ankiDue = view.findViewByIdentifier<TextView>("$PACKAGE_NAME:id/widget_due")?.text
        val ankiETA = view.findViewByIdentifier<TextView>("$PACKAGE_NAME:id/widget_eta")?.text

        saveToDataStore(context!!.dataStore, "widget_due", ankiDue)
        saveToDataStore(context!!.dataStore, "widget_eta", ankiETA)

        // Notify target about new data
        SmartspacerComplicationProvider.notifyChange(provideContext(), AnkiProgressComplication::class.java)
    }

    override fun getAppWidgetProviderInfo(smartspacerId: String): AppWidgetProviderInfo? {
        return getProvider(provideContext(), PACKAGE_NAME, PROVIDER_CLASS)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config()
    }
}