package com.example.arcoreapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment
    var isModelPlaced = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        arFragment.arSceneView.scene.addOnUpdateListener {

            if (isModelPlaced)
                return@addOnUpdateListener
            
            var frame = arFragment.arSceneView.arFrame!!

            var planes = frame.getUpdatedTrackables(Plane::class.java)

            for (plane in planes) {
                if (plane.trackingState == TrackingState.TRACKING) {
                    var anchore = plane.createAnchor(plane.centerPose)

                    makeCube(anchore)

                    break
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun makeCube(anchor: Anchor) {
        isModelPlaced = true
        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.RED))
            .thenAccept {
                var cubeRenderable = ShapeFactory.makeCube(
                    Vector3(0.3f, 0.3f, 0.3f),
                    Vector3(0f, 0f, 0f), it
                )

                var node = AnchorNode(anchor)
                node.renderable = cubeRenderable
                arFragment.arSceneView.scene.addChild(node)
            }
    }

}
