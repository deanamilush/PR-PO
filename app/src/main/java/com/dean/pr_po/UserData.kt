package com.dean.pr_po

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData (
        var name: String = "",
        var username: String = "",
        var password: String = "",
        var poThisMonth: Int? = null,
        var poLastMonth: Int? = null,
        var poMonthAgo: Int? = null,
        var prThisMonth: Int? = null,
        var prLastMonth: Int? = null,
        var prMonthAgo: Int? = null,
        var version: Int? = null,
        var dev: Int? = null
) :Parcelable