package ui.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import complications.AnkiProgressComplication
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.theme.getColorScheme
import nodomain.pacjo.smartspacer.plugin.utils.PreferenceInput
import nodomain.pacjo.smartspacer.plugin.utils.SettingsTopBar
import nodomain.pacjo.smartspacer.plugin.utils.getStringFromDataStore
import nodomain.pacjo.smartspacer.plugin.utils.saveToDataStore
import providers.dataStore

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            val template = getStringFromDataStore(context.dataStore, "complication_template") ?: "Anki {eta} / {due}"

            MaterialTheme (
                // Change default colorScheme to our dynamic one
                colorScheme = getColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {

                        SettingsTopBar((context as? Activity)!!,"Anki")

                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Progress complication",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PreferenceInput(
                                icon = R.drawable.pencil,
                                title = "Complication template",
                                subtitle = "Control how complication shows data",
                                stateCallback = {
                                    value -> saveToDataStore(context.dataStore,"complication_template", value)
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
    }
}