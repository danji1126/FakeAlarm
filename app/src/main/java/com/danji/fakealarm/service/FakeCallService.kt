package com.danji.fakealarm.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Vibrator
import android.widget.Toast
import com.danji.fakealarm.activity.MainActivity


class FakeCallService : Service() {

    internal var mNM: NotificationManager? = null

    internal var isRunning = true

    internal var vibrator: Vibrator? = null

    private var mp: MediaPlayer? = null

    override fun onCreate() {

        // vibrate
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val intent = Intent(baseContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("Alarm", true)
        val pi = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        pi.send()




        /*
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
        */
    }

    override fun onDestroy() {

        isRunning = false

        mp!!.stop()

        // Tell the user we stopped.
        Toast.makeText(this, "종료합니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

}

