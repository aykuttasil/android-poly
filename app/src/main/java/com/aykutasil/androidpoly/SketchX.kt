package com.aykutasil.androidpoly


import ketai.sensors.KetaiLocation
import processing.core.PApplet

class SketchX : PApplet()  {

    private lateinit var ketaiLocation: KetaiLocation

    override fun settings() {

    }

    override fun setup() {
        /*
        ketai.ui.KetaiAlertDialog.popup(
            this, "title"
            , "test"
        )
        */

        ketaiLocation = KetaiLocation(this)
        
        background(red(20), green(30), blue(60))
        stroke(red(20), green(30), blue(60))
        fill(red(20), green(30), blue(60))
    }

    override fun draw() {

    }

    override fun mousePressed() {
        super.mousePressed()
        kotlin.io.println("Clicked: $mouseX , $mouseY")
    }

}