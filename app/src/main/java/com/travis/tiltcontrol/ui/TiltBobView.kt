package com.travis.tiltcontrol.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun TiltBobView(
    offsetX: Float,
    offsetY: Float,
    threshold: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(240.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val boundaryRadius = size.minDimension / 2f * 0.9f

        // Outer ring represents the tilt boundary
        drawCircle(
            color = Color.Gray,
            radius = boundaryRadius,
            center = center,
            style = Stroke(width = 4f)
        )

        // Scale raw offset values so the threshold maps to the ring edge
        val scale = if (threshold != 0f) boundaryRadius / threshold else 0f
        val rawBobOffset = Offset(offsetX * scale, offsetY * scale)

        // Clamp the dot so it never visually leaves the ring, even if the
        // real tilt value is far past the threshold
        val distance = sqrt(rawBobOffset.x * rawBobOffset.x + rawBobOffset.y * rawBobOffset.y)
        val clampedOffset = if (distance > boundaryRadius && distance > 0f) {
            rawBobOffset * (boundaryRadius / distance)
        } else {
            rawBobOffset
        }

        val currentTiltMagnitude = sqrt(offsetX * offsetX + offsetY * offsetY)
        val isTilted = currentTiltMagnitude >= threshold

        drawCircle(
            color = if (isTilted) Color.Red else Color(0xFF2196F3),
            radius = 20f,
            center = center + clampedOffset
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TiltBobViewPreview() {
    TiltBobView(offsetX = 0.5f, offsetY = 0.8f, threshold = 2.5f)
}