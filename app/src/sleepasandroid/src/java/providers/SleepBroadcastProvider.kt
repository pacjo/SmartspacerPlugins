package providers

import android.content.Intent
import android.content.IntentFilter
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.DataStoreManager.Companion.broadcastEventKey
import nodomain.pacjo.smartspacer.plugin.utils.save
import targets.SleepMessagesTarget
import targets.SleepMessagesTarget.Companion.dataStore

class SleepBroadcastProvider: SmartspacerBroadcastProvider() {

    companion object {
        const val PACKAGE_NAME = "com.urbandroid.sleep"

        const val INTENT_TIME_TO_BED = "com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO"
        const val INTENT_ALARM_DISMISS = "com.urbandroid.sleep.alarmclock.ALARM_ALERT_DISMISS_AUTO"
    }

    override fun onReceive(intent: Intent) {
        // save data
        provideContext().dataStore.save(broadcastEventKey, intent.action.toString())

        SmartspacerTargetProvider.notifyChange(context!!, SleepMessagesTarget::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(
                IntentFilter(INTENT_TIME_TO_BED),
                IntentFilter(INTENT_ALARM_DISMISS)
            )
        )
    }
}