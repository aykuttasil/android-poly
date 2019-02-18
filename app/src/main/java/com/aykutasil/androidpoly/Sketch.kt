package com.aykutasil.androidpoly

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape

class Sketch : PApplet() {
    var angle = 0f
    var cube: PShape? = null

    override fun settings() {
        fullScreen(PConstants.P3D)
    }

    override fun setup() {
        fullScreen(PConstants.P3D)
        val tex = loadImage("mosaic.jpg")
        cube = createShape(PConstants.BOX, 400.toFloat())
        cube?.setTexture(tex)
    }

    override fun draw() {
        background(0x81B771)
        lights()
        translate((width / 2).toFloat(), (height / 2).toFloat())
        rotateY(angle)
        rotateX(angle * 2)
        shape(cube)
        angle += 0.01f
    }
}