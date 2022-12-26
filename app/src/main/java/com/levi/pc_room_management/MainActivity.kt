package com.levi.pc_room_management


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    val items = arrayOf("문제 없음", "인터넷이 느림", "게임이 잘 안됨", "모니터 이상","본체 이상","키보드 이상", "마우스 이상")

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database.reference

        //알람 빌더
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder : NotificationCompat.Builder

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = "one-channel"
            val channelName = "My Channel One"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            //채널에 대한 정보
            channel.description = "My Channel One Description"
            channel.setShowBadge(true)

            var uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            channel.setSound(uri,audioAttributes)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100,200,100,200)

            //채널을 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            //채널을 이용해 빌더 생성
            builder = NotificationCompat.Builder(this,channelId)

        }
        else{
            builder = NotificationCompat.Builder(this)
        }

//        val intent = Intent(this,MainActivity::class.java)
//        val pedingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
//        builder.setContentIntent(pedingIntent)

        //알람 객체 생성
        builder.setSmallIcon(R.drawable.gradient_bar)
        builder.setWhen(System.currentTimeMillis())
        builder.setContentTitle("PC Room")
        builder.setContentText("변동사항이 있습니다.")

        val idToNameMap = mapOf(
            R.id.r601 to "R601",
            R.id.r605 to "R605",
            R.id.r701 to "R701",
            R.id.r705 to "R705",
            R.id.r801 to "R801",
            R.id.r805 to "R805",
            R.id.r806 to "R806",
            R.id.r901 to "R901",
            R.id.r905 to "R905",
            R.id.r906 to "R906",
            R.id.r1001 to "R1001",
            R.id.r1005 to "R1005",
            R.id.r1006 to "R1006",
            R.id.r1101 to "R1101",
            R.id.r1102 to "R1102",
            R.id.r1103 to "R1103"
        )

        val buttonIds = arrayOf(R.id.r601, R.id.r605, R.id.r701, R.id.r705, R.id.r801, R.id.r805, R.id.r806,
            R.id.r901, R.id.r905, R.id.r906, R.id.r1001, R.id.r1005, R.id.r1006, R.id.r1101, R.id.r1102, R.id.r1103)
        val buttons = buttonIds.map { findViewById<Button>(it) }

        fun setOnClickListeners() {
            for (button in buttons) {
                button.setOnClickListener {

                    AlertDialog.Builder(this).run {
                        setTitle("PC에 어떤 문제가 있나요?")
                        setSingleChoiceItems(items, 0, object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                val id = button.id
                                val name = idToNameMap[id]
                                database.child("${name}")
                                    .setValue(items[p1])
                                    button.setBackgroundColor(Color.parseColor("#FF9F29"))
                            }
                        })
                        setPositiveButton("닫기", null)
                        show()
                    }
                }
            }
        }
        setOnClickListeners()

        fun setValueEventListeners() {
            for (button in buttons) {
                val id = button.id
                val name = idToNameMap[id]
                database.child("${name}").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // 데이터가 변경되었습니다.
                        database.child("${name}").get().addOnSuccessListener {
                            if ("${it.value}" == "해결완료") {
                                button.setBackgroundColor(Color.parseColor("#ADE792"))


                                manager.notify(11, builder.build())
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // 오류가 발생했습니다.
                    }
                })
            }
        }
        setValueEventListeners()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem1: MenuItem? = menu?.add(0, 0, 0, "원격지원 요청")
        val menuItem2: MenuItem? = menu?.add(0, 1, 0, "관리자에게 메세지 남기기")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        0 -> {
            Toast.makeText(this, "관리자에게 원격지원을 요청했습니다.", Toast.LENGTH_SHORT).show()
            true
        }
        1 -> {
            Toast.makeText(this, "미구현", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}

