package com.danji.certainalarm.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.danji.certainalarm.R
import com.danji.certainalarm.model.Contact
import com.danji.certainalarm.service.AlarmCallService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    var mCalendar: GregorianCalendar? = null
    lateinit var mp: MediaPlayer
    lateinit var am: AudioManager
    lateinit var task: LoadingTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCalendar = GregorianCalendar()

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        mp = MediaPlayer.create(applicationContext, notification)
        am = getSystemService(Context.AUDIO_SERVICE) as AudioManager


    }

    override fun onResume() {
        super.onResume()

        var mAlarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //저장
        btnSave.setOnClickListener {
            var hour: Int = 0
            var minute: Int = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.hour
                minute = timePicker.minute
            } else {
                hour = timePicker.currentHour
                minute = timePicker.currentMinute
            }

            //전화번호 가져오기
            var contact = GetRandomContact()
            if (contact == null) {
                Toast.makeText(this, "전화를 할 연락처가 없습니다. ㅠ.ㅠ", Toast.LENGTH_SHORT).show()
            } else {
                //시간 가져오기
                (mCalendar as GregorianCalendar).set(GregorianCalendar.HOUR_OF_DAY, hour)
                (mCalendar as GregorianCalendar).set(GregorianCalendar.MINUTE, minute)

                //체크박스 가져오기
                var alIntent = Intent(this, AlarmCallService::class.java)
                alIntent.putExtra("telNo", contact.phonenum)
                alIntent.putExtra("telIndc", checkBox.isChecked)

                //알람설정
                val pIntent = PendingIntent.getService(this@MainActivity, 0, alIntent, 0)
                //test code
//                mAlarmMgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pIntent)
//                mAlarmMgr.set(AlarmManager.RTC, mCalendar!!.timeInMillis, pIntent)
                var showText = hour.toString() + " : " + minute.toString() + " / " + contact.phonenum

                val oneday = (24 * 60 * 60 * 1000).toLong()// 24시간
                mAlarmMgr.setRepeating(AlarmManager.RTC, mCalendar!!.timeInMillis, oneday, pIntent)

                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //실행 테스트 코드
        btnRun.setOnClickListener {
//                var tm: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactList[idx].phonenum)))
//                val pIntent = PendingIntent.getActivity(this@MainActivity, 0, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactList[idx].phonenum)), 0)
//                mAlarmMgr.set(AlarmManager.RTC, (mCalendar as GregorianCalendar).timeInMillis, pIntent)
//                val oneday = (24 * 60 * 60 * 1000).toLong()// 24시간
//                mAlarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis()+1000, oneday, pIntent)
//                finish()
        }

        //액티비티가 다시 실행 되었을때 팝업 호출
        if (intent != null && intent.extras != null) {
            var alarmIndc = intent.extras.getBoolean("alarm")
            if (alarmIndc) {
                var telNo = intent.extras.getString("telNo")
                var telIndc = intent.extras.getBoolean("telIndc")
                //초기화
                intent.putExtra("alarm", false)
                intent.putExtra("telNo", false)
                intent.putExtra("telIndc", false)
                var maxVolum = am.getStreamMaxVolume(AudioManager.STREAM_RING)
                mp.setVolume(maxVolum.toFloat(), maxVolum.toFloat())
                am.setStreamVolume(AudioManager.STREAM_RING, maxVolum, maxVolum)
                mp!!.start()

                var pd = ProgressDialog(this)
                pd.setTitle("알람")
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                pd.setMessage("빨리 끄지 않으면...")
                pd.setCancelable(true)
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "알람끄기",
                        DialogInterface.OnClickListener { dialog, which ->
                            task.cancel(true)
                            Toast.makeText(this,
                                    "일어나셨군요.",
                                    Toast.LENGTH_SHORT).show()
                        })

                task = LoadingTask(pd, this, mp, telNo, telIndc)
                task.execute()
            }
        }
    }

    class LoadingTask(pd: ProgressDialog, context: Context, mp: MediaPlayer, telNo: String, telIndc: Boolean) : AsyncTask<String, Int, Boolean>() {
        val TAG = "LoadingTask"
        private var pd: ProgressDialog? = null
        private var context: Context? = null
        private var mp: MediaPlayer? = null
        private var telIndc: Boolean = false
        private var telNo: String = ""

        init {
            this.pd = pd
            this.context = context
            this.mp = mp
            this.telNo = telNo
            this.telIndc = telIndc
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pd?.show()
        }

        override fun doInBackground(vararg params: String): Boolean? {
            try {
                for (i in 0..10) {
                    pd!!.setProgress(i * 10)
                    Thread.sleep(500)
                }
            } catch (e: InterruptedException) {
                Log.d(TAG, "Exception : " + e.localizedMessage)
            }
            return java.lang.Boolean.TRUE
        }

        override fun onCancelled(result: Boolean?) {
            super.onCancelled(result)
            Log.d(TAG, "onCancelled : " + result!!)
            mp?.stop()
            pd?.dismiss()
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            Log.d(TAG, "onPostExecute : " + result!!)
            mp?.stop()
            if (telIndc) {
                context!!.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNo)))
            } else {
                context!!.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telNo)))
            }
            pd?.dismiss()

        }
    }

    //주소록 중 1개 연락처 랜덤으로 가져오기
    private fun GetRandomContact(): Contact {
        var idx = 0
        var rtn: Contact? = null
        var contactList = getContactList()
        if (contactList.size > 0) {
            var random = Random()
            idx = random.nextInt(contactList?.size!!)
            rtn = contactList?.get(idx)
        }
        return rtn as Contact
    }

    //주소록 전화번호
    private fun getContactList(): ArrayList<Contact> {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER, // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) // 연락처 이름.

        val selectionArgs: Array<String>? = null
        val sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        val contactCursor = contentResolver.query(uri, projection, null, selectionArgs, sortOrder)
        val contactlist = ArrayList<Contact>()

        if (contactCursor.moveToFirst()) {
            do {
                var phonenumber = contactCursor.getString(1).replace("-", "")
                if (phonenumber.length > 9) { //112, 119와 같은 번호는 List 에서 제외
                    if (phonenumber.length == 10) {
                        phonenumber = phonenumber.substring(0, 3) + "-" + phonenumber.substring(3, 6) + "-" + phonenumber.substring(6)
                    } else if (phonenumber.length > 8) {
                        phonenumber = phonenumber.substring(0, 3) + "-" + phonenumber.substring(3, 7) + "-" + phonenumber.substring(7)
                    }

                    val acontact = Contact()
                    acontact.photoid = contactCursor.getLong(0)
                    acontact.phonenum = phonenumber
                    acontact.name = contactCursor.getString(2)
                    contactlist.add(acontact)
                }
            } while (contactCursor.moveToNext())
        }

        return contactlist
    }
}
