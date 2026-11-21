package targets

import android.content.ComponentName
import android.content.Intent
import android.icu.util.Calendar
import androidx.core.graphics.drawable.toIcon
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.hideTargetWithoutComplicationsKey
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import ui.activities.ConfigurationActivity
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

class LivelyGreetingTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val hideNoComplications = provideContext().dataStore.get(hideTargetWithoutComplicationsKey) == true

        val morningString = context!!.resources.getStringArray(R.array.quickspace_psa_morning)
        val lateEveningStrings = context!!.resources.getStringArray(R.array.quickspace_psa_late_evening)
        val earlyEveningStrings = context!!.resources.getStringArray(R.array.quickspace_psa_early_evening)
        val midnightStrings = context!!.resources.getStringArray(R.array.quickspace_psa_midnight)
        val afternoonStrings = context!!.resources.getStringArray(R.array.quickspace_psa_noon)
        val randomStrings = context!!.resources.getStringArray(R.array.quickspace_psa_random)

        val hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // every hour of the day maps to exactly one bucket, used for both greeting and funny text
        val timeOfDayGreeting = context!!.resources.getString(when (hourOfDay) {
            in 5..11 -> R.string.quickspace_grt_morning
            in 12..15 -> R.string.quickspace_grt_afternoon
            in 16..22 -> R.string.quickspace_grt_evening
            else -> R.string.quickspace_grt_night       // 23..4
        })

        val timeOfDayStrings = when (hourOfDay) {
            in 5..11 -> morningString
            in 12..15 -> afternoonStrings
            in 16..18 -> earlyEveningStrings
            in 19..22 -> lateEveningStrings
            else -> midnightStrings                     // 23..4
        }

        // half the time pick from the time-specific pool, otherwise from the general one
        val funnyGreeting = (if (Random.nextBoolean()) timeOfDayStrings else randomStrings).random()

        val currentDateTime = LocalDateTime.now()
        val currentDay = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).lowercase()

        val greeting = when (Random.nextInt(100)) {
            in 0..1 -> provideContext().getString(R.string.quickspace_ext_one, currentDay)     // 2% chance
            in 2..3 -> provideContext().getString(R.string.quickspace_ext_two, currentDay)     // 2% chance
            in 4..24 -> timeOfDayGreeting                                                      // 20% chance

            else -> funnyGreeting
        }

        return listOf(TargetTemplate.Basic(
            id = "greeting_target_$smartspacerId",
            componentName = ComponentName(provideContext(), LivelyGreetingTarget::class.java),
            title = Text(greeting),
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