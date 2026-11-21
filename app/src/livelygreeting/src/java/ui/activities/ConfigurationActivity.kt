package ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import data.DataStoreManager.Companion.changeOnUnlockKey
import data.DataStoreManager.Companion.dataStore
import data.DataStoreManager.Companion.hideTargetWithoutComplicationsKey
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceSwitch
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save
import services.UnlockService
import targets.LivelyGreetingTarget

class ConfigurationActivity : ComponentActivity() {

    private fun startUnlockServiceIfEnabled() {
        UnlockService.update(this, dataStore.get(changeOnUnlockKey) != false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // the foreground service can only be started reliably while the app is visible,
        // and its notification needs the permission on Android 13+
        val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            startUnlockServiceIfEnabled()
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startUnlockServiceIfEnabled()
        }

        setContent {
            val context = LocalContext.current

            val hideNoComplications = context.dataStore.get(hideTargetWithoutComplicationsKey) == true
            val changeOnUnlock = context.dataStore.get(changeOnUnlockKey) != false

            PluginTheme {
                PreferenceLayout("Lively Greeting") {
                    PreferenceHeading("Greeting target")

                    PreferenceSwitch(
                        iicon = CommunityMaterial.Icon.cmd_eye_off,
                        title = "Dynamically hide",
                        description = "Hide target when no complications are available",
                        onCheckedChange = { value ->
                            context.dataStore.save(hideTargetWithoutComplicationsKey, value)

                            SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
                        },
                        checked = hideNoComplications
                    )

                    PreferenceSwitch(
                        iicon = CommunityMaterial.Icon.cmd_cellphone_lock,
                        title = "Change on unlock",
                        description = "Show a new message every time the phone is unlocked",
                        onCheckedChange = { value ->
                            context.dataStore.save(changeOnUnlockKey, value)
                            UnlockService.update(context, value)

                            SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
                        },
                        checked = changeOnUnlock
                    )
                }
            }
        }
    }
}