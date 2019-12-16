package id.pamoyanan_dev.gitsalarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import id.pamoyanan_dev.gitsalarm.NotificationUtil.createNotificationChannel
import java.text.SimpleDateFormat
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            Log.d("TAGTIMENOW", getTimeNow())
            // pengecekan dilakukan agar notifikasi tidak muncul berulang
            if (getTimeNow() == getTimefromSchedule(intent.getStringExtra("validationTime").toLong())) {
                if (context != null) createNotificationChannel(
                    context,
                    getTimefromSchedule(intent.getStringExtra("validationTime").toLong())
                )
            }

            if (intent.action == "android.intent.action.TIME_SET") {
                context?.stopService(Intent(context, AlarmService::class.java))

                // langkah ini dilakukan untuk memicu ulang agar service kembali menyala
                // setelah melakukan uji coba mengganti tanggal service mati
                Handler().postDelayed({
                    context?.startService(
                        Intent(
                            context,
                            AlarmService::class.java
                        )
                    )
                }, 1000)
            }
        }
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