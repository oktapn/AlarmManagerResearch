package id.pamoyanan_dev.gitsalarm

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    var id: String,
    var time: Long,
    var status: Boolean
) : Parcelable

