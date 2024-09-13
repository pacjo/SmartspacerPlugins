package complications

import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import data.SharedDataStoreManager.Companion.batteryLevelKey
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import providers.BatteryBroadcastProvider.Companion.dataStore

class BatteryLevelComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val level = provideContext().dataStore.get(batteryLevelKey)

        return if (level != null) listOf(
            ComplicationTemplate.Basic(
                id = "battery_level_complication_$smartspacerId",
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.battery
                    )
                ),
                content = Text("$level%"),
                onClick = TapAction(intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
            ).create()
        ) else emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Battery level complication",
            description = "Shows battery level",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery),
            broadcastProvider = "${BuildConfig.APPLICATION_ID}.broadcast.battery"
        )
    }
}