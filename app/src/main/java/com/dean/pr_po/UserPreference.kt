package com.dean.pr_po

import android.content.Context

internal class UserPreference(context: Context) {

    companion object{
        private const val PREFS_NAME = "user_pref"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserData) {
        val editor = preferences.edit()
        editor.putString(USERNAME, value.username)
        editor.putString(PASSWORD, value.password)
        editor.apply()
    }

    fun deleteUser(value: UserData){
        val edit = preferences.edit()
        edit.remove(USERNAME)
        edit.remove(PASSWORD)
        edit.clear()
        edit.apply()
    }

    fun getUser(): UserData {
        val model = UserData()
        model.username = preferences.getString(USERNAME, "")
        model.password = preferences.getString(PASSWORD, "")

        return model
    }
}