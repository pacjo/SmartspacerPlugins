package providers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerRequirementProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import complications.BatteryLevelComplication
import complications.ChargingStatusComplication
import data.SharedDataStoreManager.Companion.DATASTORE_NAME
import data.SharedDataStoreManager.Companion.batteryChargingTimeRemainingKey
import data.SharedDataStoreManager.Companion.batteryCurrentKey
import data.SharedDataStoreManager.Companion.batteryIsChargingKey
import data.SharedDataStoreManager.Companion.batteryLevelKey
import data.SharedDataStoreManager.Companion.batteryStatusKey
import data.SharedDataStoreManager.Companion.batteryVoltageKey
import nodomain.pacjo.smartspacer.plugin.utils.save
import requirements.ChargingRequirement
import targets.LocalBatteryTarget

class BatteryBroadcastProvider: SmartspacerBroadcastProvider() {

    companion object {
        // it's here, because we share the datastore between target and complications
        // and this is the only shared part
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
    }

    override fun onReceive(intent: Intent) {
        val batteryManager = context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context!!.registerReceiver(null, filter)
        }

        val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        val isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL ||
            batteryManager.isCharging
        val chargingTimeRemaining = batteryManager.computeChargeTimeRemaining()
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val level = (
            (
                100f *
                batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)!!) /
                batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            ).toInt()

        // save result
        provideContext().dataStore.run {
            save(batteryStatusKey, status)
            save(batteryIsChargingKey, isCharging)
            save(batteryChargingTimeRemainingKey, chargingTimeRemaining)
            save(batteryVoltageKey, voltage)
            save(batteryCurrentKey, current)
            save(batteryLevelKey, level)
        }

        // notify about change
        SmartspacerTargetProvider.notifyChange(context!!, LocalBatteryTarget::class.java)
        SmartspacerComplicationProvider.notifyChange(context!!, ChargingStatusComplication::class.java)
        SmartspacerComplicationProvider.notifyChange(context!!, BatteryLevelComplication::class.java)
        SmartspacerRequirementProvider.notifyChange(context!!, ChargingRequirement::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        )
    }
}