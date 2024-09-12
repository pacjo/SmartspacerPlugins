package ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.AnkiProgressComplication
import data.DataStoreManager.Companion.complicationTemplateKey
import data.DataStoreManager.Companion.dataStore
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceHeading
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceInput
import nodomain.pacjo.smartspacer.plugin.ui.components.PreferenceLayout
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.save

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val template = context.dataStore.get(complicationTemplateKey) ?: "Anki {eta} / {due}"

            PluginTheme {
                PreferenceLayout("Anki") {
                    PreferenceHeading("Messages target")

                    PreferenceInput(
                        icon = R.drawable.pencil,
                        title = "Complication template",
                        description = "Control how complication shows data",
                        onTextChange = { value ->
                            context.dataStore.save(complicationTemplateKey, value)

                            SmartspacerComplicationProvider.notifyChange(context, AnkiProgressComplication::class.java)
                        },
                        dialogText = "Use: \n" +
                                "- '{due}' to show remaining card count\n" +
                                "- '{eta}' to show estimated completion time\n" +
                                "keep the text short, it can overlap with other complications\n" +
                                "(you can use emojis)",
                        defaultText = template
                    )
                }
            }
        }
    }
}