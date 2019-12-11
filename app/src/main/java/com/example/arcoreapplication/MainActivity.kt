package com.example.arcoreapplication

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity(), Scene.OnUpdateListener {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onUpdate(frameTime: FrameTime) {
        var frame = arFragment.arSceneView.arFrame
        var images = frame?.getUpdatedTrackables(AugmentedImage::class.java)

        for (image in images!!){
            if (image.trackingState == TrackingState.TRACKING){
                if (image.name == "fox"){
                    var anchor =image.createAnchor(image.centerPose)
                    createModel(anchor)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createModel(anchor: Anchor){
        ModelRenderable.builder()
            .setSource(this@MainActivity,Uri.parse("ArcticFox_Posed.sfb"))
            .build()
            .thenAccept {
                placeModel(it,anchor)
            }
    }

    private fun placeModel(model: ModelRenderable?, anchor: Anchor) {
        var node = AnchorNode(anchor)
        node.renderable = model
        arFragment.arSceneView.scene.addChild(node)
    }

    lateinit var arFragment: CustomARFragment

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as CustomARFragment
        arFragment.arSceneView.scene.addOnUpdateListener(this)

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            var anchor = hitResult.createAnchor()
            ModelRenderable.builder()
                .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
                .build()
                .thenAccept {
                    addModelToScene(anchor, it)
                }
                .exceptionally {
                    var builder = AlertDialog.Builder(this@MainActivity)
                    builder.setMessage(it.message.toString())
                    builder.show()
                    null
                }
        }
    }

    fun setupDatabase(config: Config, session: Session): Unit {
        var foxBitmap = BitmapFactory.decodeResource(resources, R.drawable.fox)
        var aid = AugmentedImageDatabase(session)
        aid.addImage("fox", foxBitmap)
        config.augmentedImageDatabase = aid
    }

    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        var anchorNode = AnchorNode(anchor)
        var transformableNode = TransformableNode(arFragment.transformationSystem)
        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }
}
