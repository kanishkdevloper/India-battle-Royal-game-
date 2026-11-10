package com.dustline.game

import kotlin.math.sqrt

/**
 * Reload animation - ORIGINAL, written for Dustline (no third-party motion data).
 * Replaces the earlier data that came from a file with no recorded licence.
 *
 * u = 0..1 over the whole reload:
 *   0.00-0.20  gun tips toward the player and rolls slightly (sway)
 *   0.12-0.30  spare magazine slides out of the magazine well and drops away
 *   0.30-0.50  hold
 *   0.50-0.72  new magazine travels back up into the gun
 *   0.62-0.74  small "knock" when the magazine seats
 *   0.80-1.00  gun returns to the aiming pose
 *
 * out layout (same as before, so GameRenderer is unchanged):
 *   [0..2] gun position delta (metres), [3..6] gun rotation quaternion xyzw, [7..9] spare-magazine offset (xyz).
 */
object ReloadAnim {
    const val N = 67   // kept for compatibility (frame count of the old data)

    private fun sm(a: Float, b: Float, x: Float): Float {
        val t = ((x - a) / (b - a)).coerceIn(0f, 1f); return t * t * (3f - 2f * t)
    }

    fun sample(u: Float, out: FloatArray) {
        val x = u.coerceIn(0f, 1f)
        val tilt = minOf(sm(0f, 0.2f, x), 1f - sm(0.8f, 1f, x))
        val knock = sm(0.62f, 0.68f, x) * (1f - sm(0.68f, 0.76f, x))
        val mag = sm(0.12f, 0.30f, x) - sm(0.50f, 0.72f, x)

        out[0] = 0.004f * tilt
        out[1] = 0.020f * tilt - 0.008f * knock
        out[2] = -0.030f * tilt + 0.006f * knock

        var qx = 0.030f * tilt + 0.012f * knock
        var qy = 0.015f * tilt
        var qz = -0.120f * tilt - 0.020f * knock
        var qw = 1f
        val l = sqrt(qx * qx + qy * qy + qz * qz + qw * qw)
        qx /= l; qy /= l; qz /= l; qw /= l
        out[3] = qx; out[4] = qy; out[5] = qz; out[6] = qw

        out[7] = 0f
        out[8] = -0.39f * mag
        out[9] = 0.19f * mag
    }

    /** blend in / out at the start and end of the reload */
    fun weight(u: Float): Float = minOf(sm(0f, 0.1f, u), 1f - sm(0.9f, 1f, u))
}
