package com.example.arcoreapplication

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            placeModel(hitResult.createAnchor())
        }
    }

    private fun placeModel(anchor: Anchor?) {

        ModelRenderable.builder()
            .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
            .setRegistryId("3DMODEL")
            .build()
            .thenAccept {
                addNodeToScene(it,anchor)
            }.exceptionally {
                var builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(it.message)
                builder.show()
                null
            }

    }

    private fun addNodeToScene(modelRenderable: ModelRenderable?, anchor: Anchor?) {
        var node = AnchorNode(anchor)
        node.renderable = modelRenderable
        arFragment.arSceneView.scene.addChild(node)
    }

}
