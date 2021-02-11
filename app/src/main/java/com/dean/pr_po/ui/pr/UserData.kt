package com.dean.pr_po.ui.pr

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData (
        var name: String = "",
        var username: String = "",
        var password: String = "",
        var thismonth: Int = 0,
        var lastmonth: Int = 0,
        var threemonth: Int = 0
) :Parcelable