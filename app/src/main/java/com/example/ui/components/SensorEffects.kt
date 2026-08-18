package com.example.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs

/**
 * Custom hook to read device gyroscope data for 2.5D Parallax depth effects.
 * Creates an offset mapping device tilt to screen space translation.
 */
@Composable
fun rememberDeviceTilt(sensitivity: Float = 1.5f): State<Offset> {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
    val tiltOffset = remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(sensorManager) {
        if (sensorManager == null) return@DisposableEffect onDispose {}

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    
                    // orientationAngles[1] is pitch, orientationAngles[2] is roll
                    val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                    val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
                    
                    // Smooth and clamp the values to create a subtle parallax offset
                    val maxOffset = 30f
                    
                    // Dampen small movements to avoid jitter
                    val smoothRoll = if (abs(roll) < 1f) 0f else roll
                    val smoothPitch = if (abs(pitch) < 1f) 0f else pitch

                    val offsetX = (smoothRoll * sensitivity).coerceIn(-maxOffset, maxOffset)
                    // Invert pitch so tilting device forward moves foreground up
                    val offsetY = (-smoothPitch * sensitivity).coerceIn(-maxOffset, maxOffset)

                    tiltOffset.value = Offset(offsetX, offsetY)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        
        onDispose { 
            if (sensor != null) {
                sensorManager.unregisterListener(listener) 
            }
        }
    }

    return tiltOffset
}
