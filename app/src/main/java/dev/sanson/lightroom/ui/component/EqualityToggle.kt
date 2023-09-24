package dev.sanson.lightroom.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme

enum class Equality { GreaterThan, EqualTo, LessThan }

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

@Composable
fun EqualityToggle(
    equality: Equality,
    onEqualityChange: (Equality) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.onSurface
    val widthPx = 36f

    val equalityHeight =
        remember { Animatable(initialValue = if (equality == Equality.EqualTo) 0f else 24f) }
    val topPathRotation =
        remember { Animatable(initialValue = if (equality == Equality.LessThan) 180f else 0f) }

    LaunchedEffect(equality) {
        when (equality) {
            Equality.GreaterThan -> {
                equalityHeight.animateTo(0f)
                topPathRotation.snapTo(0f)
                equalityHeight.animateTo(24f)
            }

            Equality.EqualTo -> {
                equalityHeight.animateTo(0f)
            }

            Equality.LessThan -> {
                equalityHeight.animateTo(0f)
                topPathRotation.snapTo(180f)
                equalityHeight.animateTo(24f)
            }
        }
    }

    Canvas(
        modifier
            .size(36.dp)
            .clip(RoundedCornerShape(50))
            .clickable { onEqualityChange(equality.next()) }
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                shape = RoundedCornerShape(50),
            ),
    ) {
        val totalHeight = equalityHeight.value + 16f
        translate(left = (size.width - 36f) / 2f, top = (size.height - totalHeight) / 2f) {
            rotate(topPathRotation.value, pivot = Offset(widthPx / 2f, equalityHeight.value / 2f)) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)

                        lineTo(x = widthPx, y = equalityHeight.value / 2f)
                        lineTo(x = 0f, y = equalityHeight.value)
                    },
                    color = lineColor,
                    style = Stroke(
                        width = 5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }

            drawLine(
                brush = SolidColor(lineColor),
                start = Offset(x = 0f, y = equalityHeight.value + 16f),
                end = Offset(x = 36f, y = equalityHeight.value + 16f),
                cap = StrokeCap.Round,
                strokeWidth = 5f,
            )
        }
    }
}

@DarkModePreviews
@Composable
fun EqualityTogglePreview() {
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