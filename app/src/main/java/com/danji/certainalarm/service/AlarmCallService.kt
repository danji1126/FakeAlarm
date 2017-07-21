package com.danji.certainalarm.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.danji.certainalarm.activity.MainActivity


class AlarmCallService : Service() {


/*
    internal var isRunning = true

    internal var vibrator: Vibrator? = null

    private var mp: MediaPlayer? = null
*/


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intentMain = Intent(baseContext, MainActivity::class.java)
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intentMain.putExtra("alarm", true)
        intentMain.putExtra("telNo", intent!!.getStringExtra("telNo"))
        intentMain.putExtra("telIndc", intent!!.getBooleanExtra("telIndc", false))
//        startActivity(intentMain)
        val pi = PendingIntent.getActivity(baseContext, 0, intentMain, PendingIntent.FLAG_ONE_SHOT)
        pi.send()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        // vibrate
//        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onDestroy() {
/*
        isRunning = false
        mp!!.stop()
*/
        // Tell the user we stopped.
//        Toast.makeText(this, "종료합니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

}

