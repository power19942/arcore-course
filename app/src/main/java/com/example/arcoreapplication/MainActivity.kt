package com.example.arcoreapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment


@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            createViewRenderable(hitResult.createAnchor())
        }
    }

    private fun createViewRenderable(anchor: Anchor?) {
        ViewRenderable
            .builder()
            .setView(this@MainActivity, R.layout.my_view)
            .build()
            .thenAccept {
                addToScene(it, anchor)
            }
    }

    private fun addToScene(viewRenderable: ViewRenderable?, anchor: Anchor?) {
        var node = AnchorNode(anchor)
        node.renderable = viewRenderable
        arFragment.arSceneView.scene.addChild(node)

        var view = viewRenderable!!.view

        var viewPager = view.findViewById<ViewPager>(R.id.view_pager)

        var images =
            listOf<Int>(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground)

        var adapter = Adapter()
        adapter.images = images

        viewPager.adapter = adapter
    }

    inner class Adapter : PagerAdapter() {

        lateinit var images: List<Int>

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun getCount(): Int = images.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var view = layoutInflater.inflate(R.layout.pager_item, container, false)
            var img = view.findViewById<ImageView>(R.id.img)
            img.setImageResource(images.get(position))
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as ImageView)
        }

    }

}
