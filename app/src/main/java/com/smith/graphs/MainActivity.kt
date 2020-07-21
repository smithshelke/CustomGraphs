package com.smith.graphs

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemBars()
        setupInsets()
        progress_circular.start()
//        graph.setOnTouchListener(object : Graph.OnTouchListener {
//            override fun onTouch(x: Int, y: Int) {
//                point.text = "X : $x"
//            }
//
//        })
//        add.setOnClickListener{
//            graph.addPoint((100*Math.random()).toFloat())
//        }

    }

    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
    }

    private fun setupInsets() {
        toolbar.setOnApplyWindowInsetsListener { view, insets ->
            view.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.apply {
                        minimumHeight = height + insets.systemWindowInsetTop
                        updatePadding(top = insets.systemWindowInsetTop)
                    }.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
            return@setOnApplyWindowInsetsListener insets
        }
    }
}
