package id.pamoyanan_dev.gitsalarm

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import id.pamoyanan_dev.gitsalarm.Contant._ID
import java.text.SimpleDateFormat
import java.util.*


class AlarmService : Service() {

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var sch: ArrayList<Schedule> = ArrayList()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.getSerializableExtra(_ID) != null) {
            sch = intent.getSerializableExtra(_ID) as ArrayList<Schedule>
        }
        setupTimer()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun setupTimer() {
        timer = Timer()

        // menjadwalkan alarm per satu jam sekali
        setupTimerTask()

        timer?.schedule(timerTask, 1000, 3600000)
    }

    private fun setupTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                for (item in 0 until sch.size) {
                    setScheduleNotification(sch[item])
                }
            }
        }
    }

    private fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setScheduleNotification(schedule: Schedule) {
        // membuat objek intent yang mana akan menjadi target selanjutnya
        // bisa untuk berpindah halaman dengan dan tanpa data.
        val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
        intent.putExtra("validationTime",  schedule.time.toString())
        Log.e("TAGSID", schedule.id)
        Log.e("TAGSTIME",  schedule.time.toString())
        Log.e("TAGSSTATUS",  schedule.status.toString())

        // membuat objek PendingIntent yang berguna sebagai penampung intent dan aksi yang akan dikerjakan
        val requestCode = 0
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext,  schedule.id.toInt(), intent, FLAG_UPDATE_CURRENT)

        // membuat objek AlarmManager untuk melakukan pendataran alarm yang akan dijadwalkan
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // kita buat alarm yang dapat berfungsi walaupun dalam kondisi hp idle dan tepat waktu
        Log.e("TIMETAG", getTimeNow() + getTimefromSchedule( schedule.time))
        if (getTimeNow() < getTimefromSchedule( schedule.time)) {
            Log.e("TAGALARMCREATED",  schedule.id)
            if ( schedule.status != false) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    schedule.time,
                    pendingIntent
                )
            } else {
                Log.e("TAGALARMCANCELED",  schedule.id)
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopTimer()
    }

    private fun getTimeNow(): String {
        val dateTimeMillis = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeMillis

        return SimpleDateFormat("dd MMM yyyy h:mm a").format(calendar.time)
    }

    private fun getTimefromSchedule(long: Long): String {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = long

        return SimpleDateFormat("dd MMM yyyy h:mm a").format(calendar.time)
    }

}