package com.danji.fakealarm.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.widget.Toast
import com.danji.fakealarm.R
import com.danji.fakealarm.model.Contact
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var mCalendar: GregorianCalendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var contactList = getContactList()
        var idx = 0
        mCalendar = GregorianCalendar()

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

            (mCalendar as GregorianCalendar).set(GregorianCalendar.HOUR_OF_DAY, hour)
            (mCalendar as GregorianCalendar).set(GregorianCalendar.MINUTE, minute)

//            fullTime = hour.toString() + " : " + minute.toString()

            var showText = ""
            if (contactList.size > 0) {
                var random = Random()
                idx = random.nextInt(contactList.size)
                showText += "idx=" + idx
                showText += " value1=" + contactList[idx].name
                showText += " value2=" + contactList[idx].phonenum
                showText += " value3=" + contactList[idx].photoid

            }

            Toast.makeText(this, showText, Toast.LENGTH_SHORT).show()

        }

        //실행
        btnRun.setOnClickListener {
//            val pIntent = PendingIntent.getActivity(this@MainActivity, 0, Intent(this, FakeCallService::class.java), 0)
            if (contactList.size > 0) {
                var tm: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactList[idx].phonenum)))
                val pIntent = PendingIntent.getActivity(this@MainActivity, 0, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactList[idx].phonenum)), 0)
                mAlarmMgr.set(AlarmManager.RTC, (mCalendar as GregorianCalendar).timeInMillis, pIntent)

            } else {
                Toast.makeText(this, "연락처가 없습니다. ㅠ.ㅠ", Toast.LENGTH_SHORT).show()

            }

        }
    }

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
                if(phonenumber.length > 3){ //112, 119와 같은 번호는 List 에서 제외
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
