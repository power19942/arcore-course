package com.example.arcoreapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    lateinit var modelRenderable: ModelRenderable
    lateinit var texture: Texture
    var isAdded = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ModelRenderable.builder()
            .setSource(this, R.raw.fox_face)
            .build()
            .thenAccept {
                modelRenderable = it
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false

            }

        Texture.builder()
            .setSource(this, R.drawable.fox_face_mesh_texture)
            .build()
            .thenAccept {
                this.texture = it!!
            }
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as CustomArFragment
        arFragment.arSceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        arFragment.arSceneView.scene.addOnUpdateListener {
            if (modelRenderable == null || texture == null)
                return@addOnUpdateListener

            var frame = arFragment.arSceneView.arFrame!!
            var faces = frame.getUpdatedTrackables(AugmentedFace::class.java)
            for (face in faces) {
                if (isAdded)
                    return@addOnUpdateListener

                var faceNode = AugmentedFaceNode(face)
                faceNode.setParent(arFragment.arSceneView.scene)
                faceNode.faceRegionsRenderable = modelRenderable
                faceNode.faceMeshTexture = texture
                isAdded = true
            }
        }
    }

}
