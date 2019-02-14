package com.aykutasil.androidpoly

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.kittinunf.fuel.httpDownload
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.jsonDeserializer
import com.github.kittinunf.fuel.rx.rxResponseObject
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import processing.android.PFragment
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import java.io.File

/**
 * https://code.tutsplus.com/tutorials/how-to-use-free-3d-models-from-google-poly-in-android-apps--cms-31356
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val key = "AIzaSyBgWDaqt4l1hW_bIMRhZvsPDU0H4Fv5mTU"
        const val baseURL = "https://poly.googleapis.com/v1"
    }

    val listURL = "$baseURL/assets"
    // some asset id
    val assetID = "assets/3yiIERrKNQr"

    // its url
    val assetURL = "$baseURL/$assetID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetList.setOnClickListener {
            getAssetList()
        }

        btnGetAsset.setOnClickListener {
            getAsset()
        }

        btnLoad.setOnClickListener {
            loadAsset()
        }
    }

    private fun loadAsset() {
        val fragment = PFragment(canvas)
        fragment.setView(canvas_holder, this@MainActivity)
    }

    private val canvas = object : PApplet() {
        var myPolyAsset: PShape? = null

        override fun settings() {
            fullScreen(PConstants.P3D)
        }

        override fun setup() {
            myPolyAsset = loadShape(File(filesDir, "asset.obj").absolutePath)
        }

        override fun draw() {
            background(0)
            scale(-50f)
            translate(-4f, -14f)

            shape(myPolyAsset)
        }
    }

    @SuppressLint("CheckResult")
    private fun getAsset() {
        assetURL.httpGet(listOf("key" to key))
            .rxResponseObject(jsonDeserializer())
            .subscribeOn(Schedulers.io())
            .subscribe({
                val asset = it.obj()

                var objFileURL: String? = null
                var mtlFileURL: String? = null
                var mtlFileName: String? = null

                val formats = asset.getJSONArray("formats")

                // Loop through all formats
                for (i in 0 until formats.length()) {
                    val currentFormat = formats.getJSONObject(i)

                    // Check if current format is OBJ
                    if (currentFormat.getString("formatType") == "OBJ") {
                        // Get .obj file details
                        objFileURL = currentFormat.getJSONObject("root")
                            .getString("url")

                        // Get the first .mtl file details
                        mtlFileURL = currentFormat.getJSONArray("resources")
                            .getJSONObject(0)
                            .getString("url")

                        mtlFileName = currentFormat.getJSONArray("resources")
                            .getJSONObject(0)
                            .getString("relativePath")
                        break
                    }
                }

                // download and store obj file as asset.obj
                objFileURL!!.httpDownload().fileDestination { _, _ ->
                    File(filesDir, "asset.obj")
                }.response { _, _, result ->
                    result.fold({

                    }, {
                        Log.e("POLY", "An error occurred")
                    })
                }

                // download and store mtl file without
                // changing its name
                mtlFileURL!!.httpDownload().fileDestination { _, _ ->
                    File(filesDir, mtlFileName)
                }.response { _, _, result ->
                    result.fold({

                    }, {
                        Log.e("POLY", "An error occurred")
                    })
                }
            }, {
                Log.e("POLY", "An error occurred")
            })
    }


    @SuppressLint("CheckResult")
    fun getAssetList() {
        listURL.httpGet(
            listOf(
                "category" to "animals",
                "key" to key,
                "format" to "OBJ"
            )
        )
            .rxResponseObject(jsonDeserializer())
            .subscribeOn(Schedulers.io())
            .subscribe({
                // Get assets array
                val assets = it.obj().getJSONArray("assets")

                // Loop through array
                for (i in 0 until assets.length()) {
                    // Get id and displayName
                    val id = assets.getJSONObject(i).getString("name")
                    val displayName =
                        assets.getJSONObject(i).getString("displayName")

                    // Print id and displayName
                    Log.d("POLY", "(ID: $id) -- (NAME: $displayName)")
                }
            }, {
                // In case of an error
                Log.e("POLY", "An error occurred")
            })

    }
}
