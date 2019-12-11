package com.example.arcoreapplication

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment
    lateinit var videoRenderable: ModelRenderable
    lateinit var player: MediaPlayer
    var HEIGHT = 1.25f
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var texture = ExternalTexture()
        player = MediaPlayer.create(this@MainActivity, R.raw.video)
        player.setSurface(texture.surface)
        player.isLooping = true
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        ModelRenderable.builder()
            .setSource(this, R.raw.video_screen)
            .build()
            .thenAccept {
                videoRenderable = it
                videoRenderable.material.setExternalTexture("videoTexture", texture)
                videoRenderable.material.setFloat4("keyColor", Color(0.01843f, 1.0f, 0.098f))
            }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            var node = AnchorNode(hitResult.createAnchor())
            if (!player.isPlaying) {
                player.start()

                texture.surfaceTexture.setOnFrameAvailableListener {
                    node.renderable = videoRenderable
                    texture.surfaceTexture.setOnFrameAvailableListener(null)
                }
            } else {
                node.renderable = videoRenderable
            }

            var width = player.videoWidth
            var height = player.videoHeight

            node.localScale = Vector3(
                HEIGHT * (width / height), HEIGHT, 1.0f
            )

            arFragment.arSceneView.scene.addChild(node)
        }
    }

}
