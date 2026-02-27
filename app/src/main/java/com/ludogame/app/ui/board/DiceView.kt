package com.ludogame.app.ui.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

// Pip positions as (xFraction, yFraction) within the dice face
private val pipPositions = mapOf(
    1 to listOf(0.5f to 0.5f),
    2 to listOf(0.70f to 0.28f, 0.30f to 0.72f),
    3 to listOf(0.70f to 0.28f, 0.5f to 0.5f, 0.30f to 0.72f),
    4 to listOf(0.30f to 0.28f, 0.70f to 0.28f, 0.30f to 0.72f, 0.70f to 0.72f),
    5 to listOf(0.30f to 0.28f, 0.70f to 0.28f, 0.5f to 0.5f, 0.30f to 0.72f, 0.70f to 0.72f),
    6 to listOf(0.30f to 0.22f, 0.70f to 0.22f, 0.30f to 0.5f, 0.70f to 0.5f, 0.30f to 0.78f, 0.70f to 0.78f)
)

@Composable
fun DiceView(
    value: Int,
    isRollEnabled: Boolean,
    onRoll: () -> Unit,
    pipColor: Color = Color(0xFF1A1A2E),
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 10f * density
            }
            .clickable(
                enabled = isRollEnabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onRoll()
                scope.launch {
                    rotation.snapTo(0f)
                    rotation.animateTo(
                        targetValue = 720f,
                        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                    )
                    rotation.snapTo(0f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDice(value, pipColor)
        }
    }
}

private fun DrawScope.drawDice(value: Int, pipColor: Color) {
    val w = size.width
    val h = size.height
    val inset = w * 0.05f          // offset so shadow peeks behind
    val corner = w * 0.18f
    val faceW = w - inset
    val faceH = h - inset

    // --- Drop shadow ---
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.30f),
        topLeft = Offset(inset, inset),
        size = Size(faceW, faceH),
        cornerRadius = CornerRadius(corner)
    )

    // --- Dice face: gradient for 3D depth (bright top-left → muted bottom-right) ---
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFAFAFA), Color(0xFFDDDDDD)),
            start = Offset(0f, 0f),
            end = Offset(faceW, faceH)
        ),
        topLeft = Offset(0f, 0f),
        size = Size(faceW, faceH),
        cornerRadius = CornerRadius(corner)
    )

    // --- Top-left specular highlight ---
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.55f), Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(faceW * 0.45f, faceH * 0.45f)
        ),
        topLeft = Offset(0f, 0f),
        size = Size(faceW, faceH),
        cornerRadius = CornerRadius(corner)
    )

    // --- Border ---
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.12f),
        topLeft = Offset(0f, 0f),
        size = Size(faceW, faceH),
        cornerRadius = CornerRadius(corner),
        style = Stroke(width = 1.5f)
    )

    // --- Pips ---
    if (value in 1..6) {
        val pipR = w * 0.088f
        pipPositions[value]!!.forEach { (xf, yf) ->
            val cx = faceW * xf
            val cy = faceH * yf
            // pip shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.20f),
                radius = pipR,
                center = Offset(cx + pipR * 0.18f, cy + pipR * 0.18f)
            )
            // pip body with subtle gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(pipColor.copy(alpha = 0.85f), pipColor),
                    center = Offset(cx - pipR * 0.25f, cy - pipR * 0.25f),
                    radius = pipR
                ),
                radius = pipR,
                center = Offset(cx, cy)
            )
        }
    } else {
        // No value yet — draw a subtle question-mark-ish dot in center
        drawCircle(
            color = Color.Black.copy(alpha = 0.08f),
            radius = w * 0.12f,
            center = Offset(faceW * 0.5f, faceH * 0.5f)
        )
    }
}
