package com.aykutasil.androidpoly

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v4.content.ContextCompat
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import java.io.File


class SketchPoly : PApplet() {
    private var manager: SensorManager? = null
    private var sensor: Sensor? = null
    private var listener: AccelerometerListener? = null
    private var myPolyAsset: PShape? = null

    var ax: Float = 0.toFloat()
    var ay: Float = 0.toFloat()
    var az: Float = 0.toFloat()

    inner class AccelerometerListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            ax = event.values[0]
            ay = event.values[1]
            az = event.values[2]

            //Log.i("aaa", ax.toString())
            //translate(ax, ay)
            //redraw()
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun settings() {
        fullScreen(PConstants.P3D)
    }

    override fun setup() {
        manager = ContextCompat.getSystemService(context!!, SensorManager::class.java)
        sensor = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        listener = AccelerometerListener()
        manager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)

        myPolyAsset = loadShape(File(context.filesDir, "asset.obj").absolutePath)
    }

    override fun onResume() {
        super.onResume()
        manager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        manager?.unregisterListener(listener)
    }

    override fun draw() {

        ellipse(mouseX.toFloat(), mouseY.toFloat(), (mouseX - mouseY).toFloat(), (mouseY - mouseX).toFloat())
        /*
        background(0)
        scale(-60f)
        translate(-ax-6, -ay-4, az)

        shape(myPolyAsset)
        */
    }
}