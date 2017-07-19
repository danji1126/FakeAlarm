package com.danji.fakealarm.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import com.danji.fakealarm.R
import kotlinx.android.synthetic.main.activity_fake_ringing.*


class FakeRingingActivity : AppCompatActivity() {

    private var networkCarrier: String? = null
    private var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_ringing)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        networkCarrier = tm.networkOperatorName

        if (networkCarrier != null) {
            textView1.text = "Incoming call - " + networkCarrier!!
        } else {
            textView1.text = "Incoming call"
        }

        val callNumber = getContactNumber()
        val callName = getContactName()

        chosenfakename.text = callName
        chosenfakenumber.text = callNumber

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        mp = MediaPlayer.create(applicationContext, notification)
        mp!!.start()

        answercall.setOnClickListener {
            mp!!.stop()
        }
        rejectcall.setOnClickListener {

            mp!!.stop()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_fake_ringing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()

//        if (id == R.id.action_settings) {
//            return true
//        }
        return super.onOptionsItemSelected(item)
    }

    private fun getContactNumber(): String {
        var contact: String? = null
        val myIntent = intent
        val mIntent = myIntent.extras
        if (mIntent != null) {
            contact = mIntent.getString("myfakenumber")
        }
        return contact.toString()
    }

    private fun getContactName(): String {
        var contactName: String? = null
        val myIntent = intent
        val mIntent = myIntent.extras
        if (mIntent != null) {
            contactName = mIntent.getString("myfakename")
        }
        return contactName.toString()
    }


}
