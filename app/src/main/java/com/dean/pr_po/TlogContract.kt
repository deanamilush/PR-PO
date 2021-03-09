package com.dean.pr_po

import android.provider.BaseColumns

internal class TlogContract {

    internal class TlogColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "tlog"
            const val _ID = "_id"
            const val ID_APP = "id_app"
            const val ID_USER  = "id_user"
            const val ID_CONN = "id_conn"
        }
    }
}