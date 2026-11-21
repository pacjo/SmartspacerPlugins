package receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.DataStoreManager.Companion.changeOnUnlockKey
import data.DataStoreManager.Companion.dataStore
import nodomain.pacjo.smartspacer.plugin.utils.get
import targets.LivelyGreetingTarget

// Manifest-declared so it can wake the plugin process after Android killed it
class UnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return
        if (context.dataStore.get(changeOnUnlockKey) == false) return

        Log.d("LivelyGreeting", "Unlock detected, refreshing target")
        SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
    }
}
