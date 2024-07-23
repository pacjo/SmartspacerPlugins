package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.getRandom
import nodomain.pacjo.smartspacer.plugin.utils.getRandomFromPairs
import nodomain.pacjo.smartspacer.plugin.utils.getStringFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.imageTargetAdjustDrawable
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore
import providers.SleepBroadcastProvider.Companion.INTENT_ALARM_DISMISS
import providers.SleepBroadcastProvider.Companion.INTENT_TIME_TO_BED
import providers.SleepBroadcastProvider.Companion.PACKAGE_NAME
import ui.activities.ConfigurationActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "status_target_data")

class SleepMessagesTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val simpleStyle = getBoolFromDataStore(context!!.dataStore, "simple_style") ?: false
        val showTimeToBed = getBoolFromDataStore(context!!.dataStore, "show_time_to_bed") ?: true
        val showAlarmDismissed = getBoolFromDataStore(context!!.dataStore, "show_alarm_dismissed") ?: true

        val event = getStringFromDataStore(context!!.dataStore, "event")

        val (title, subtitle) = when {
            (event == INTENT_TIME_TO_BED && showTimeToBed) ->
                listOf(
                    Pair("Time to bed.", ""),
                    Pair("Goodnight.", ""),
                    Pair("It's late.", "")
                ).getRandomFromPairs()
            (event == INTENT_ALARM_DISMISS && showAlarmDismissed) ->
                listOf(
                    Pair("Still sleepy?", ""),
                    Pair("Morning!", "How have you slept?"),
                    Pair("It's never enough", "isn't it?")
                ).getRandomFromPairs()
            else -> Pair("", "")
        }

        if (title != "") {
            return if (simpleStyle) {
                listOf(TargetTemplate.Basic(
                    id = "example_$smartspacerId",
                    componentName = ComponentName(
                        provideContext(),
                        SleepMessagesTarget::class.java
                    ),
                    title = Text("$title $subtitle"),
                    subtitle = null,
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            provideContext(),
                            when (event) {
                                INTENT_TIME_TO_BED -> R.drawable.bed_outline
                                else -> R.drawable.tea_outline
                            }
                        )
                    ),
                    onClick = TapAction(intent = Intent(context!!.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)))
                ).create().apply {
                    canTakeTwoComplications = true
                })
            } else {
                listOf(TargetTemplate.Image(
                    context = provideContext(),
                    id = "example_$smartspacerId",
                    componentName = ComponentName(
                        provideContext(),
                        SleepMessagesTarget::class.java
                    ),
                    title = Text(title),
                    subtitle = Text(subtitle),
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            provideContext(),
                            when (event) {
                                INTENT_TIME_TO_BED -> R.drawable.bed_outline
                                else -> R.drawable.tea_outline
                            }
                        )
                    ),
                    image = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        imageTargetAdjustDrawable(
                            context!!,
                            when (event) {
                                INTENT_TIME_TO_BED ->
                                    listOf(
                                        R.drawable.saa_read,
                                        R.drawable.saa_sleep,
                                        R.drawable.saa_tooth
                                    ).getRandom()
                                else ->
                                    listOf(
                                        R.drawable.saa_food,
                                        R.drawable.saa_home,
                                        R.drawable.saa_work
                                    ).getRandom()
                            }
                        )
                    ),
                    onClick = TapAction(intent = Intent(context!!.packageManager.getLaunchIntentForPackage(PACKAGE_NAME))
                    )
                ).create())
            }
        } else return emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Sleep messages",
            description = "Shows messages from Sleep as Android app",
            icon = Icon.createWithResource(provideContext(), R.drawable.sleep_as_android),
            configActivity = Intent(context, ConfigurationActivity::class.java),
            compatibilityState = getCompatibilityState(context, PACKAGE_NAME, "Sleep as Android isn't installed"),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.sleepasandroid.broadcast.sleep"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        // remove event from datastore
        saveToDataStore(context!!.dataStore, "event")

        notifyChange(provideContext(), SleepMessagesTarget::class.java)

        return true
    }
}