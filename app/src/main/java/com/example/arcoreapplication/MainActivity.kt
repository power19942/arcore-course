package com.example.arcoreapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.RED))
            .thenAccept {
                var renderable = ShapeFactory.makeSphere(
                    0.1f,
                    Vector3(0f, 0f, 0f), it
                )

                var node = Node()
                node.setParent(arFragment.arSceneView.scene)
                node.renderable = renderable
                arFragment.arSceneView.scene.addOnUpdateListener {
                    var camera = arFragment.arSceneView.scene.camera!!
                    // get screen center
                    var ray = camera.screenPointToRay(1080 / 2f, 1920 / 2f)
                    var newPosition = ray.getPoint(1f) // 1f away from screen
                    node.localPosition = newPosition
                }
            }
    }

}
