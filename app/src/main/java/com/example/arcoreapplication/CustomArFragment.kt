package com.example.arcoreapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment  : ArFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var frameLayout = super.onCreateView(inflater, container, savedInstanceState) as FrameLayout
        // remove hand motion when app start
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return frameLayout
    }

    override fun getSessionConfiguration(session: Session?): Config {
        var config = Config(session)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        arSceneView.setupSession(session)
        return config
    }

    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return super.getSessionFeatures()
    }
}