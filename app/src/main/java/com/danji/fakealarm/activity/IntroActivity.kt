package com.danji.fakealarm.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.danji.fakealarm.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
        var introRunnable = Runnable {
            run {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        var handler = Handler()
        handler.postDelayed(introRunnable, 1000)
    }
}
