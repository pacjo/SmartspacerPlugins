package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.model.CompatibilityState
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import java.io.File

class SleepMessagesTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {

        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val showTimeToBed = preferences.optBoolean("show_time_to_bed", true)
        val showAlarmDismissed = preferences.optBoolean("show_alarm_dismissed", true)

        // get event (intent.action)
        val event = jsonObject.optString("event")

        val title = when {
            (event == "com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO" && showTimeToBed) -> "Time to bed."
            (event == "com.urbandroid.sleep.alarmclock.ALARM_ALERT_DISMISS_AUTO" && showAlarmDismissed) -> "Good morning!"
            else -> ""
        }

        Log.i("pacjodebug", "event: $event, title: $title")

        if (title != "") {
            // TODO: maybe add image target?
            return listOf(TargetTemplate.Basic(
                id = "example_$smartspacerId",
                componentName = ComponentName(
                    provideContext(),
                    SleepMessagesTarget::class.java
                ),
                title = Text(title),
                subtitle = Text(""),
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        when (event) {
                            "com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO" -> R.drawable.bed_outline
                            else -> R.drawable.tea_outline
                        }
                    )
                ),
                onClick = TapAction(intent = Intent(context!!.packageManager.getLaunchIntentForPackage("com.urbandroid.sleep")))
            ).create().apply {
                canTakeTwoComplications = true
            })
        } else return emptyList()
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Sleep As Android messages",
            description = "Shows messages from Sleep as Android app",
            icon = Icon.createWithResource(provideContext(), R.drawable.sleep),
            configActivity = Intent(context, ConfigurationActivity::class.java),
            compatibilityState = getCompatibilityState(),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.sleepasandroid.broadcast.sleep"
        )
    }

    private fun getCompatibilityState(): CompatibilityState {
        // https://stackoverflow.com/questions/6758841/how-can-i-find-if-a-particular-package-exists-on-my-android-device
        return if (context?.packageManager?.getInstalledApplications(0)?.find { info -> info.packageName == "com.urbandroid.sleep" } == null) {
            CompatibilityState.Incompatible("Sleep as Android isn't installed")
        } else CompatibilityState.Compatible
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        val file = File(context?.filesDir, "data.json")

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // remove event from data file, since we don't want to show the target all day
        jsonObject.put("event", "")
        file.writeText(jsonObject.toString())

        Log.i("pacjodebug", "ondismiss")
        notifyChange(context!!, SleepMessagesTarget::class.java)
        return true
    }
}