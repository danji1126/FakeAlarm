package com.danji.fakealarm.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.danji.fakealarm.activity.FakeCallActivity


class FakeCallService : Service() {

    internal var mNM: NotificationManager? = null

    internal var isRunning = true

    internal var vibrator: Vibrator? = null

    override fun onCreate() {

        // vibrate
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        try {
            val intent = Intent(baseContext, FakeCallActivity::class.java)
            val pi = PendingIntent.getActivity(
                    baseContext, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            pi.send()
        } catch (e: CanceledException) {
            e.printStackTrace()
        }

        val triggerService = Thread(Runnable {
            // TODO Auto-generated method stub
            while (isRunning) {
                try {

                    Log.e("Androday", "Ring ~ ")

                    (vibrator as Vibrator).vibrate(1000)

                    Thread.sleep(2000)


                } catch (e: Exception) {
                    // TODO: handle exception
                    Log.i("MyServiceIntent", e.message)
                }

            }

            //한번만 울리고 서비스 종료 하려면
            // Done with our work... stop the service!
            //AlarmService_Service.this.stopSelf();
        })

        triggerService.start()
    }

    override fun onDestroy() {

        isRunning = false

        // Tell the user we stopped.
        Toast.makeText(this, "종료합니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

}

