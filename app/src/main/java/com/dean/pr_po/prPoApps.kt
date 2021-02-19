package com.dean.pr_po

import android.app.Application

class prPoApps: Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}