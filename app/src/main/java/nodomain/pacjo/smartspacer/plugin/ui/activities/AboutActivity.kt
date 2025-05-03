package nodomain.pacjo.smartspacer.plugin.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.simpleicons.SimpleIcons
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.ui.theme.PluginTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PluginTheme {
                Scaffold {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(stringResource(R.string.app_name) + " " + stringResource(R.string.app_description))

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IIconLink(
                                asset = SimpleIcons.Icon.sim_github,
                                url = stringResource(R.string.github_url),
                                contentDescription = stringResource(R.string.github)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            IIconLink(
                                asset = SimpleIcons.Icon.sim_mastodon,
                                url = stringResource(R.string.mastodon_url),
                                contentDescription = stringResource(R.string.mastodon)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            IIconLink(
                                asset = SimpleIcons.Icon.sim_paypal,
                                url = stringResource(R.string.paypal_url),
                                contentDescription = stringResource(R.string.paypal)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IIconLink(asset: IIcon, url: String, contentDescription: String?) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            }
    ) {
        Image(
            asset = asset,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
    }
}