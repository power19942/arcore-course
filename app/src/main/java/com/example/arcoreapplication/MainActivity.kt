package com.example.arcoreapplication


import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import java.util.function.Consumer


class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment
    lateinit var texture: ExternalTexture
    lateinit var mediaPlayer: MediaPlayer
    lateinit var scene: Scene
    lateinit var renderable: ModelRenderable
    var isImageDetected = false
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as CustomArFragment


        texture = ExternalTexture()

        mediaPlayer = MediaPlayer.create(this, R.raw.video)
        mediaPlayer.setSurface(texture.surface)
        mediaPlayer.isLooping = true


        ModelRenderable
            .builder()
            .setSource(this, Uri.parse("video_screen.sfb"))
            .build()
            .thenAccept(Consumer { modelRenderable: ModelRenderable ->
                modelRenderable.material.setExternalTexture(
                    "videoTexture",
                    texture
                )
                modelRenderable.material.setFloat4(
                    "keyColor",
                    Color(0.01843f, 1f, 0.098f)
                )
                renderable = modelRenderable
            })

        scene = arFragment.getArSceneView().getScene();

        scene.addOnUpdateListener{frameTime ->
            if (isImageDetected) return@addOnUpdateListener

            val frame: Frame? = arFragment.arSceneView.arFrame

            val augmentedImages: Collection<AugmentedImage> =
                frame!!.getUpdatedTrackables(
                    AugmentedImage::class.java
                )


            for (image in augmentedImages) {
                if (image.trackingState == TrackingState.TRACKING) {
                    if (image.name == "image") {
                        isImageDetected = true
                        playVideo(
                            image.createAnchor(image.centerPose), image.extentX,
                            image.extentZ
                        )
                        break
                    }
                }
            }
        }
    }

    private fun playVideo(
        anchor: Anchor,
        extentX: Float,
        extentZ: Float
    ) {
        mediaPlayer.start()
        val anchorNode = AnchorNode(anchor)
        texture.surfaceTexture
            .setOnFrameAvailableListener { surfaceTexture: SurfaceTexture? ->
                anchorNode.setRenderable(renderable)
                texture.surfaceTexture.setOnFrameAvailableListener(null)
            }
        anchorNode.setWorldScale(Vector3(extentX, 1f, extentZ))
        scene.addChild(anchorNode)
    }

}
