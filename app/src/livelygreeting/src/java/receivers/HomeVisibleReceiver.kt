package receivers

import android.content.Context
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.receivers.SmartspacerVisibilityChangedReceiver
import data.DataStoreManager.Companion.changeOnHomeKey
import data.DataStoreManager.Companion.dataStore
import nodomain.pacjo.smartspacer.plugin.utils.get
import targets.LivelyGreetingTarget

/**
 * Smartspacer tells plugins when Smartspace becomes visible or hidden (e.g. when the user
 * returns to the home screen from an app), so no extra permission is needed to detect it.
 */
class HomeVisibleReceiver : SmartspacerVisibilityChangedReceiver() {

    override fun onSmartspaceVisibilityChanged(context: Context, visible: Boolean, timestamp: Long) {
        if (!visible) return
        if (context.dataStore.get(changeOnHomeKey) == false) return

        Log.d("LivelyGreeting", "Smartspace became visible, refreshing target")
        SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
    }
}
