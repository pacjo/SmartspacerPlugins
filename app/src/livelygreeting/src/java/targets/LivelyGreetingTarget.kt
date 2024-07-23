package targets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.core.graphics.drawable.toIcon
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.getBoolFromDataStore
import ui.activities.ConfigurationActivity
import kotlin.random.Random

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "greeting_target_settings")

class LivelyGreetingTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val hideNoComplications = getBoolFromDataStore(provideContext().dataStore, "target_hide_no_complications") ?: false

        val morningString = context!!.resources.getStringArray(R.array.quickspace_psa_morning)
        val lateEveningStrings = context!!.resources.getStringArray(R.array.quickspace_psa_late_evening)
        val earlyEveningStrings = context!!.resources.getStringArray(R.array.quickspace_psa_early_evening)
        val midnightStrings = context!!.resources.getStringArray(R.array.quickspace_psa_midnight)
        val afternoonStrings = context!!.resources.getStringArray(R.array.quickspace_psa_noon)
        val randomStrings = context!!.resources.getStringArray(R.array.quickspace_psa_random)

        val hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val timeOfDayGreeting = when (hourOfDay) {
            in 0..3 -> context!!.resources.getString(R.string.quickspace_grt_night)
            in 5..10 -> context!!.resources.getString(R.string.quickspace_grt_morning)
            in 12..15 -> context!!.resources.getString(R.string.quickspace_grt_afternoon)
            in 16..20 -> context!!.resources.getString(R.string.quickspace_grt_evening)
            in 21..23 -> context!!.resources.getString(R.string.quickspace_grt_night)
            else -> context!!.resources.getString(R.string.quickspace_grt_general)
        }

        val funnyGreeting = when (hourOfDay) {
            in 0..3 -> midnightStrings[Random.nextInt(0, midnightStrings.size - 1)]
            in 5..10 -> morningString[Random.nextInt(0, morningString.size - 1)]
            in 12..15 -> afternoonStrings[Random.nextInt(0, afternoonStrings.size - 1)]
            in 16..18 -> earlyEveningStrings[Random.nextInt(0, earlyEveningStrings.size - 1)]
            in 19..22 -> lateEveningStrings[Random.nextInt(0, lateEveningStrings.size - 1)]
            else -> randomStrings[Random.nextInt(0, randomStrings.size - 1)]
        }

        return listOf(TargetTemplate.Basic(
            id = "example_$smartspacerId",
            componentName = ComponentName(provideContext(), LivelyGreetingTarget::class.java),
            title = Text(when {
                    Random.nextInt(0, 100) < 15 -> timeOfDayGreeting  // about 15% chance
                    else -> funnyGreeting
                }
            ),
            subtitle = null,
            icon = null
        ).create().apply {
            canBeDismissed = false
            canTakeTwoComplications = true
            hideIfNoComplications = hideNoComplications
        })
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Lively Greeting",
            description = "Shows fun messages to brighten your day",
            icon = IconicsDrawable(provideContext(), CommunityMaterial.Icon2.cmd_human_greeting).toBitmap().toIcon(),
            allowAddingMoreThanOnce = true,
            configActivity = Intent(context, ConfigurationActivity::class.java),
            refreshPeriodMinutes = 60
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }
}
