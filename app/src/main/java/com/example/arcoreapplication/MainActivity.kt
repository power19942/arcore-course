package com.example.arcoreapplication

import android.Manifest
import android.content.pm.PackageManager
import android.media.CameraProfile
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.CameraProvider
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment
    lateinit var videoRecorder: VideoRecorder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.RED))
                .thenAccept {
                    var renderable = ShapeFactory.makeSphere(
                        0.3f, Vector3(
                            0f, 0f, 0f
                        ), it
                    )

                    var node = AnchorNode(hitResult.createAnchor())
                    node.renderable = renderable
                    arFragment.arSceneView.scene.addChild(node)
                }
        }

        videoRecorder = VideoRecorder()

        start_record.setOnClickListener {
            if (!videoRecorder.isRecording) {
                videoRecorder.setSceneView(arFragment.arSceneView)
                var orientation = resources.configuration.orientation
                videoRecorder.setVideoQuality(CameraProfile.QUALITY_MEDIUM, orientation)

                videoRecorder.startRecordingVideo()
            }
        }

        stop_record.setOnClickListener {

            if (videoRecorder.isRecording)
                videoRecorder.stopRecordingVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

}
