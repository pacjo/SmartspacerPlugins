package nodomain.pacjo.smartspacer.plugin.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import nodomain.pacjo.smartspacer.plugin.R

/**
 * Represents the root composable of all preference screens.
 *
 * @param title The title of the preference screen.
 * @param content The primary content of the preference screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceLayout(
    title: String,
    content: @Composable () -> Unit,
) {
    val activity = (LocalContext.current as? Activity)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text (
                            text = title,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton (
                            onClick = {
                                activity?.finish()
                            }
                        ) {
                            Icon (
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button_description)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) {
            Column (
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
            ) {
                content()
            }
        }
    }
}
