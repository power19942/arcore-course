package com.example.arcoreapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment


class CustomArFragment : ArFragment() {
    override fun getSessionConfiguration(session: Session): Config {
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        val aid = AugmentedImageDatabase(session)
        val image = BitmapFactory.decodeResource(resources, R.drawable.image)
        aid.addImage("image", image)
        config.augmentedImageDatabase = aid
        arSceneView.setupSession(session)
        return config
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val frameLayout =
            super.onCreateView(inflater, container, savedInstanceState) as FrameLayout?
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return frameLayout
    }
}