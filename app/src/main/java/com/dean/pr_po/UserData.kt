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
        var version: String? = null,
        var dev: String? = null,
        var pId_user: String? = null,
        var pPlant: String? = null,
        var pId_conn: String? = null,
        var pUser_sap: String? = null,
        var pPass_sap: String? = null,
        var pAshost: String? = null,
        var pSysnr: String? = null,
        var pClient: String? = null
) :Parcelable