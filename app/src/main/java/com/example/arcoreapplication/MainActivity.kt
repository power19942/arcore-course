package com.example.arcoreapplication

import android.graphics.Point
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.Texture
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {

    lateinit var arFragment: CustomArFragment
    lateinit var scene: Scene
    lateinit var camera: Camera
    lateinit var bulletRenderable: ModelRenderable

    private var shouldStartTimer = true
    private var balloonsLeft = 20
    lateinit var point: Point
    lateinit var soundPool: SoundPool
    private var sound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadSoundpool()
        point = Point()
        windowManager.defaultDisplay.getRealSize(point)
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as CustomArFragment

        scene = arFragment.arSceneView.scene
        camera = scene.camera

        addBalloonsToScene()
        buildBulletModel()

        shoot_btn.setOnClickListener {
            if (shouldStartTimer) {
                startTimer()
                shouldStartTimer = false
            }
            shoot()
        }
    }

    private fun loadSoundpool() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        sound = soundPool.load(this, R.raw.blop_sound, 1)
    }

    private fun shoot() {
        //get screen center
        val ray = camera.screenPointToRay(point.x / 2f, point.y / 2f)
        val node = Node()
        node.renderable = bulletRenderable
        scene.addChild(node)

        Thread{
            for (i in 0..199){
                val finalI = i

                runOnUiThread {
                    val vector3 = ray.getPoint(finalI * 0.1f)
                    node.worldPosition = vector3

                    val nodeInContact = scene.overlapTest(node)

                    if (nodeInContact != null) {

                        balloonsLeft--;
                        balloones_txt?.text = "Balloons Left: " + balloonsLeft
                        scene.removeChild(nodeInContact)

                        soundPool.play(sound, 1f, 1f, 1, 0
                            , 1f)

                    }
                }

                Thread.sleep(10)
                runOnUiThread { scene.removeChild(node) }
            }
        }.start()
    }

    private fun startTimer() {
        Thread {
            var seconds = 0
            while (balloonsLeft > 0){
                Thread.sleep(1000)
                seconds++

                var minutesPassed = seconds / 60
                var secondsPassed = seconds % 60

                runOnUiThread {
                    timer_txt.text = "$minutesPassed:$secondsPassed"
                }
            }
        }.start()
    }

    private fun addBalloonsToScene() {
        ModelRenderable.builder().setSource(this, Uri.parse("balloon.sfb"))
            .build()
            .thenAccept {
                for (i in 0..19) {
                    var node = Node()
                    node.renderable = it

                    scene.addChild(node)

                    val random = Random()
                    val x: Int = random.nextInt(10)
                    var z: Int = random.nextInt(10)
                    val y: Int = random.nextInt(20)

                    z = -z

                    node.worldPosition = Vector3(
                        x.toFloat(),
                        y / 10f,
                        z.toFloat()
                    )
                }
            }
    }


    private fun buildBulletModel() {
        Texture
            .builder()
            .setSource(this, R.drawable.texture)
            .build()
            .thenAccept { texture ->
                MaterialFactory
                    .makeOpaqueWithTexture(this, texture)
                    .thenAccept {
                        bulletRenderable = ShapeFactory
                            .makeSphere(
                                0.01f,
                                Vector3(0f, 0f, 0f),
                                it
                            )
                    }
            }
    }

}
