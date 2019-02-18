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
import java.io.File


/**
 * https://code.tutsplus.com/tutorials/how-to-use-free-3d-models-from-google-poly-in-android-apps--cms-31356
 */
class MainActivity : AppCompatActivity() {

    private val key = "AIzaSyBgWDaqt4l1hW_bIMRhZvsPDU0H4Fv5mTU"
    private val baseURL = "https://poly.googleapis.com/v1"

    private val listURL = "$baseURL/assets"
    private val assetID = "assets/3yiIERrKNQr"
    private val assetURL = "$baseURL/$assetID"

    private lateinit var sketch: Sketch
    private lateinit var sketchPoly: SketchPoly
    private lateinit var sketchX: SketchX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sketch = Sketch()
        sketchPoly = SketchPoly()
        sketchX = SketchX()

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        sketch.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
    }

    private fun loadAsset() {
        val fragment = PFragment(sketchX)
        fragment.setView(canvas_holder, this@MainActivity)
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
