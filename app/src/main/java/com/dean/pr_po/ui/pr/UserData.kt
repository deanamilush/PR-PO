package com.dean.pr_po.ui.pr

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData (
        var name: String = "",
        var today: Int = 0,
        var oneweek: Int = 0,
        var onemonth: Int = 0
) :Parcelable