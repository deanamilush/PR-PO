package com.dean.pr_po

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData (
        var name: String? = null,
        var username: String? = null,
        var password: String? = null,
        var poThisMonth: Int? = 0,
        var poLastMonth: Int? = 0,
        var poMonthAgo: Int? = 0,
        var prThisMonth: Int? = 0,
        var prLastMonth: Int? = 0,
        var prMonthAgo: Int? = 0,
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