package dev.sanson.lightroom.common.ui.component

import android.os.Parcelable
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.common.ui.MuzeiLightroomTheme
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Equality : Parcelable { LessThan, EqualTo, GreaterThan }

/**
 * Simple function for cycling through enum entries
 */
private fun Equality.next(): Equality {
    return if (this == Equality.entries.last()) {
        Equality.entries.first()
    } else {
        Equality.entries[ordinal + 1]
    }
}

/**
 * An Equality toggle is a control allowing choice between three equality states, as defined
 * in [Equality].
 *
 * @param equality The current equality
 * @param onEqualityChange Callback for when equality changes
 */
@Composable
fun EqualityToggle(
    equality: Equality,
    onEqualityChange: (Equality) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.onSurface

    var equalityHeightPx by remember { mutableFloatStateOf(0f) }

    val equalityHeight =
        remember { Animatable(initialValue = if (equality == Equality.EqualTo) 0f else equalityHeightPx) }
    val topPathRotation =
        remember { Animatable(initialValue = if (equality == Equality.LessThan) 180f else 0f) }

    LaunchedEffect(equality) {
        when (equality) {
            Equality.GreaterThan -> {
                equalityHeight.animateTo(0f)
                topPathRotation.snapTo(0f)
                equalityHeight.animateTo(equalityHeightPx)
            }

            Equality.EqualTo -> {
                equalityHeight.animateTo(0f)
            }

            Equality.LessThan -> {
                equalityHeight.animateTo(0f)
                topPathRotation.snapTo(180f)
                equalityHeight.animateTo(equalityHeightPx)
            }
        }
    }

    Canvas(
        modifier
            .sizeIn(minWidth = 36.dp, minHeight = 36.dp)
            .clip(RoundedCornerShape(50))
            .onSizeChanged {
                equalityHeightPx = it.height.toFloat() * 0.2f
            }
            .clickable { onEqualityChange(equality.next()) },
    ) {
        // The following draws two canvas elements - the bottom line of the lteq, gteq and eq symbol,
        // and a path which draws the top element of the symbol, allowing animation from line to
        // less than or greater than symbol.
        val totalHeight = equalityHeight.value + (size.height * 0.16f)
        val widthPx = size.width * 0.32f
        val strokeWidth = size.height * 0.04f

        translate(left = (size.width - widthPx) / 2f, top = (size.height - totalHeight) / 2f) {
            // Less than/greater than path
            rotate(topPathRotation.value, pivot = Offset(widthPx / 2f, equalityHeight.value / 2f)) {
                drawPath(
                    path =
                        Path().apply {
                            moveTo(0f, 0f)

                            lineTo(x = widthPx, y = equalityHeight.value / 2f)
                            lineTo(x = 0f, y = equalityHeight.value)
                        },
                    color = lineColor,
                    style =
                        Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                )
            }

            // Bottom line
            drawLine(
                brush = SolidColor(lineColor),
                start = Offset(x = 0f, y = totalHeight),
                end = Offset(x = widthPx, y = totalHeight),
                cap = StrokeCap.Round,
                strokeWidth = strokeWidth,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EqualityTogglePreview() {
    MuzeiLightroomTheme {
        var equality by remember { mutableStateOf(Equality.EqualTo) }

        Surface {
            EqualityToggle(
                equality = equality,
                onEqualityChange = { equality = it },
            )
        }
    }
}
