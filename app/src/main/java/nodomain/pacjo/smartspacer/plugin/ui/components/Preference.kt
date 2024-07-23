package nodomain.pacjo.smartspacer.plugin.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.iconics.compose.IconicsPainter
import com.mikepenz.iconics.typeface.IIcon

/**
 * Represents a template for all preference-related items.
 */
@Composable
fun Preference(
    @DrawableRes icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 16.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    description: (@Composable () -> Unit)? = null,
    endWidget: (@Composable () -> Unit)? = null,
) {
    Column {
        Row(
            verticalAlignment = verticalAlignment,
            modifier = modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.requiredWidth(16.dp))
            Row(
                modifier = contentModifier
                    .weight(1f),
                verticalAlignment = verticalAlignment,
            ) {
                Column(Modifier.weight(1f)) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                        LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                    ) {
                        Text(title)
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    ) {
                        description?.invoke()
                    }
                }
            }
            endWidget?.let {
                Spacer(modifier = Modifier.requiredWidth(16.dp))
                endWidget()
            }
        }
    }
}

@Composable
fun Preference(
    icon: IIcon,
    title: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 16.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    description: (@Composable () -> Unit)? = null,
    endWidget: (@Composable () -> Unit)? = null,
) {
    Column {
        Row(
            verticalAlignment = verticalAlignment,
            modifier = modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
        ) {
            Icon(
                painter = IconicsPainter(icon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.requiredWidth(16.dp))
            Row(
                modifier = contentModifier
                    .weight(1f),
                verticalAlignment = verticalAlignment,
            ) {
                Column(Modifier.weight(1f)) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                        LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                    ) {
                        Text(title)
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    ) {
                        description?.invoke()
                    }
                }
            }
            endWidget?.let {
                Spacer(modifier = Modifier.requiredWidth(16.dp))
                endWidget()
            }
        }
    }
}