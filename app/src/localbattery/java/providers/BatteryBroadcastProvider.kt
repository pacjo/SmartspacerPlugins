package providers

import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import targets.LocalBatteryTarget

class BatteryBroadcastProvider: SmartspacerBroadcastProvider() {

    override fun onReceive(intent: Intent) {
        SmartspacerTargetProvider.notifyChange(context!!, LocalBatteryTarget::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        )
    }

}