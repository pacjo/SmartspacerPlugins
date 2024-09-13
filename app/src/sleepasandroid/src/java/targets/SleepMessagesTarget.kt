package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.DataStoreManager.Companion.DATASTORE_NAME
import data.DataStoreManager.Companion.broadcastEventKey
import data.DataStoreManager.Companion.showAlarmDismissedKey
import data.DataStoreManager.Companion.showTimeToBedKey
import data.DataStoreManager.Companion.simpleStyleKey
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.getCompatibilityState
import nodomain.pacjo.smartspacer.plugin.utils.getPackageLaunchTapAction
import nodomain.pacjo.smartspacer.plugin.utils.getRandom
import nodomain.pacjo.smartspacer.plugin.utils.imageTargetAdjustDrawable
import nodomain.pacjo.smartspacer.plugin.utils.save
import providers.SleepBroadcastProvider.Companion.INTENT_ALARM_DISMISS
import providers.SleepBroadcastProvider.Companion.INTENT_TIME_TO_BED
import providers.SleepBroadcastProvider.Companion.PACKAGE_NAME
import ui.activities.ConfigurationActivity

class SleepMessagesTarget: SmartspacerTargetProvider() {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
    }

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val simpleStyle = provideContext().dataStore.get(simpleStyleKey) ?: false
        val showTimeToBed = provideContext().dataStore.get(showTimeToBedKey) ?: true
        val showAlarmDismissed = provideContext().dataStore.get(showAlarmDismissedKey) ?: true

        val event = provideContext().dataStore.get(broadcastEventKey)

        val (title, subtitle) = when {
            (event == INTENT_TIME_TO_BED && showTimeToBed) -> {
                val titles = listOf(
                    R.string.messages_time_to_bed_1,
                    R.string.messages_time_to_bed_2,
                    R.string.messages_time_to_bed_3,
                    R.string.messages_time_to_bed_4
                )

                val titleResourceId = titles.random()

                Pair(
                    first = provideContext().getString(titleResourceId),
                    second = ""
                )
            }
            (event == INTENT_ALARM_DISMISS && showAlarmDismissed) -> {
                val titles = listOf(
                    R.string.messages_alarm_dismissed_1,
                    R.string.messages_alarm_dismissed_2,
                    R.string.messages_alarm_dismissed_3,
                    R.string.messages_alarm_dismissed_4
                )

                val titleResourceId = titles.random()

                val subtitleResourceId = when (titleResourceId) {
                    R.string.messages_alarm_dismissed_3 -> R.string.messages_alarm_dismissed_3_subtitle
                    R.string.messages_alarm_dismissed_4 -> R.string.messages_alarm_dismissed_4_subtitle
                    else -> null
                }

                Pair(
                    first = provideContext().getString(titleResourceId),
                    second = subtitleResourceId?.let { provideContext().getString(it) } ?: ""
                )
            }
            else -> Pair("", "")
        }

        return if (title != "") {
             if (simpleStyle) {
                listOf(TargetTemplate.Basic(
                    id = "message_target_$smartspacerId",
                    componentName = ComponentName(
                        provideContext(),
                        SleepMessagesTarget::class.java
                    ),
                    title = Text("$title $subtitle"),
                    subtitle = null,
                    icon = null,
                    onClick = getPackageLaunchTapAction(provideContext(), PACKAGE_NAME)
                ).create().apply {
                    canTakeTwoComplications = true
                })
            } else {
                listOf(TargetTemplate.Image(
                    context = provideContext(),
                    id = "message_target_$smartspacerId",
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
                            provideContext(),
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
                    onClick = getPackageLaunchTapAction(provideContext(), PACKAGE_NAME)
                ).create())
            }
        } else emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = provideContext().getString(R.string.messages_target_title),
            description = provideContext().getString(R.string.messages_target_description),
            icon = Icon.createWithResource(provideContext(), R.drawable.sleep_as_android),
            configActivity = Intent(provideContext(), ConfigurationActivity::class.java),
            compatibilityState = getCompatibilityState(provideContext(), PACKAGE_NAME, R.string.config_incompatibility_message),
            broadcastProvider = "${BuildConfig.APPLICATION_ID}.broadcast.sleep"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        // remove event from datastore
        provideContext().dataStore.save(broadcastEventKey)

        notifyChange(smartspacerId)

        return true
    }
}