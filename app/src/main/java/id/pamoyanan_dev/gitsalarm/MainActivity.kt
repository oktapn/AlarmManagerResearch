package id.pamoyanan_dev.gitsalarm

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DECEMBER
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var sch: ArrayList<Schedule> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("TAG TIME", getTimeNow())
        addDummySchedule()
        Log.e("TAGSCH", sch.toString())

        val countDownTimer = object : CountDownTimer(6000, 1000) {
            override fun onFinish() {
                var message: String

                // alarm berulang
                message = "Alarm service dimulai"
                val intent = Intent(this@MainActivity, AlarmService::class.java)
                intent.putExtra(Contant._ID, sch)
                startService(intent)

                // alarm sekali jalan
                message = "Alarm akan menyala dalam hitungan waktu mundur"
                setScheduleNotification()

                txt_counter.text = message
            }

            override fun onTick(millisUntilFinished: Long) {
                txt_counter.text =
                    TimeUnit.SECONDS.convert(millisUntilFinished / 1000, TimeUnit.SECONDS)
                        .toString()
            }
        }
        countDownTimer.start()
    }

    private fun addDummySchedule() {
        sch.add(0, Schedule("01", 1576724400000, true))
//        sch.add(1, Schedule("02", 1576147200000, true))
//        sch.add(2, Schedule("03", 1576147500000, true))
//        sch.add(3, Schedule("04", 1576147800000, true))
//        sch.add(4, Schedule("05", 1576148100000, true))
//        sch.add(5, Schedule("06", 1576148400000, true))
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setScheduleNotification() {
        // membuat objek intent yang mana akan menjadi target selanjutnya
        // bisa untuk berpindah halaman dengan dan tanpa data.
        val intent = Intent(this@MainActivity, AboutActivity::class.java)
        intent.putExtra("key", "value")

        // membuat objek PendingIntent yang berguna sebagai penampung intent dan aksi yang akan dikerjakan
        val requestCode = 0
        val pendingIntent =
            PendingIntent.getActivity(this@MainActivity, requestCode, intent, 0)

        // membuat objek AlarmManager untuk melakukan pendataran alarm yang akan dijadwalkan
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // kita buat alarm yang dapat berfungsi walaupun dalam kondisi hp idle dan tepat waktu
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 5000,
            pendingIntent
        )
    }

    private fun getTimeNow(): String {
        val dateTimeMillis = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeMillis

        return SimpleDateFormat("dd MMM yyyy h:mm a").format(calendar.time)
    }
}
