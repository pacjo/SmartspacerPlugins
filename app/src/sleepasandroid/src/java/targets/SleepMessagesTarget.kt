package targets

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import com.kieronquinn.app.smartspacer.sdk.model.CompatibilityState
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getRandom
import nodomain.pacjo.smartspacer.plugin.utils.getRandomFromPairs
import nodomain.pacjo.smartspacer.plugin.utils.imageTargetAdjustDrawable
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import ui.activities.ConfigurationActivity
import java.io.File

class SleepMessagesTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        isFirstRun(context!!)

        val file = File(context?.filesDir, "data.json")

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val simpleStyle = preferences.optBoolean("simple_style", false)
        val showTimeToBed = preferences.optBoolean("show_time_to_bed", true)
        val showAlarmDismissed = preferences.optBoolean("show_alarm_dismissed", true)

        // get event (intent.action)
        val event = jsonObject.optString("event")

        val (title, subtitle) = when {
            (event == "com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO" && showTimeToBed) ->
                listOf(
                    Pair("Time to bed.", ""),
                    Pair("Goodnight.", ""),
                    Pair("It's late.", "")
                ).getRandomFromPairs()
            (event == "com.urbandroid.sleep.alarmclock.ALARM_ALERT_DISMISS_AUTO" && showAlarmDismissed) ->
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
                    title = Text("$title $subtitle"),   // bit of a hack, but should work
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
                                "com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO" -> R.drawable.bed_outline
                                else -> R.drawable.tea_outline
                            }
                        )
                    ),
                    image = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        imageTargetAdjustDrawable(
                            context!!,
                            when (event) {
                                "com.urbandroid.sleep.alarmclock.TIME_TO_BED_ALARM_ALERT_AUTO" ->
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
                    onClick = TapAction(intent = Intent(context!!.packageManager.getLaunchIntentForPackage("com.urbandroid.sleep"))
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

        notifyChange(context!!, SleepMessagesTarget::class.java)
        return true
    }
}