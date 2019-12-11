package com.example.arcoreapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    enum class ShapeType {
        CUBE,
        SPHERE,
        CYLINDER
    }

    var shapeType: ShapeType = ShapeType.CUBE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment


        cube.setOnClickListener {
            shapeType = ShapeType.CUBE
        }

        cylinder.setOnClickListener {
            shapeType = ShapeType.CYLINDER
        }


        sphere.setOnClickListener {
            shapeType = ShapeType.SPHERE
        }


        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            when (shapeType) {
                ShapeType.SPHERE -> {
                    placeSphere(hitResult.createAnchor())
                }

                ShapeType.CYLINDER -> {
                    placeCylinder(hitResult.createAnchor())
                }

                ShapeType.CUBE -> {
                    placeCube(hitResult.createAnchor())
                }
            }
        }
    }


    fun placeCube(anchor: Anchor) {
        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.BLUE))
            .thenAccept {
                var modelRenderable =
                    ShapeFactory.makeCube(Vector3(0.1f, 0.1f, -0.1f), Vector3(0f, 0f, 0f), it)

                placeModel(modelRenderable,anchor)
            }
    }

    fun placeSphere(anchor: Anchor) {
        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.BLUE))
            .thenAccept {
                var modelRenderable =
                    ShapeFactory.makeSphere(0.5f, Vector3(0f, 0f, 0f), it)

                placeModel(modelRenderable,anchor)
            }
    }

    fun placeCylinder(anchor: Anchor) {
        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.BLUE))
            .thenAccept {
                var modelRenderable =
                    ShapeFactory.makeCylinder(0.5f,0.5f,Vector3(0f, 0f, 0f), it)

                placeModel(modelRenderable,anchor)
            }
    }

    fun placeModel(modelRenderable: ModelRenderable, anchor: Anchor) {
        var anchorNode = AnchorNode(anchor)
        anchorNode.renderable = modelRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
    }

}
