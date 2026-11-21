package receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import data.DataStoreManager.Companion.changeOnUnlockKey
import data.DataStoreManager.Companion.dataStore
import nodomain.pacjo.smartspacer.plugin.utils.get
import services.UnlockService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        UnlockService.update(context, context.dataStore.get(changeOnUnlockKey) != false)
    }
}
