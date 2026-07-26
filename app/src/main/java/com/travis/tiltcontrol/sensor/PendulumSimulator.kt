package com.travis.tiltcontrol.sensor

import kotlin.math.min

/**
 * Simulates a weighted pendulum bob chasing a target position.
 * Each frame, a spring force pulls the bob toward the target (the
 * live accelerometer offset), while damping bleeds off energy so it
 * settles instead of oscillating forever.
 */
class PendulumSimulator(
    private val mass: Float = .5f,
    private val stiffness: Float = 60f,
    private val damping: Float = 1f
) {
    var x = 0f
        private set
    var y = 0f
        private set

    private var vx = 0f
    private var vy = 0f

    fun update(targetX: Float, targetY: Float, deltaSeconds: Float) {
        // Clamp dt so a dropped frame / lag spike can't cause a big jump
        val dt = min(deltaSeconds, 0.05f)

        val ax = (stiffness * (targetX - x) - damping * vx) / mass
        val ay = (stiffness * (targetY - y) - damping * vy) / mass

        vx += ax * dt
        vy += ay * dt
        x += vx * dt
        y += vy * dt
    }

    fun reset() {
        x = 0f
        y = 0f
        vx = 0f
        vy = 0f
    }
}