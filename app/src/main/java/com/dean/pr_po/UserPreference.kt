package com.dean.pr_po

import android.content.Context

internal class UserPreference(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
        private const val USERSAP = "user_sap"
        private const val PASSAP = "pas_sap"
        private const val ASHOST = "ashost"
        private const val SYSNR = "sysnr"
        private const val CLIENT = "client"
        private const val IDUSER = "id_user"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserData) {
        val editor = preferences.edit()
        editor.putString(USERNAME, value.username)
        editor.putString(PASSWORD, value.password)
        editor.putString(USERSAP, value.pUser_sap)
        editor.putString(PASSAP, value.pPass_sap)
        editor.putString(ASHOST, value.pAshost)
        editor.putString(SYSNR, value.pSysnr)
        editor.putString(CLIENT, value.pClient)
        editor.putString(IDUSER, value.pId_user)
        editor.apply()
    }

    fun deleteUser(value: UserData) {
        val edit = preferences.edit()
        /*edit.remove(value.username)
        edit.remove(value.password)
        edit.remove(USERNAME)
        edit.remove(PASSWORD)*/
        edit.clear()
        edit.apply()
    }

    fun getUser(): UserData {
        val model = UserData()
        model.username = preferences.getString(USERNAME, "")
        model.password = preferences.getString(PASSWORD, "")
        model.pUser_sap = preferences.getString(USERSAP, "").toString()
        model.pPass_sap = preferences.getString(PASSAP, "").toString()
        model.pAshost = preferences.getString(ASHOST, "").toString()
        model.pSysnr = preferences.getString(SYSNR, "").toString()
        model.pClient = preferences.getString(CLIENT, "").toString()
        model.pId_user = preferences.getString(IDUSER, "").toString()

        return model
    }
}
