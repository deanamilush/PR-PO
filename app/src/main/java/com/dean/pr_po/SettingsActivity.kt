package com.dean.pr_po

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.dean.pr_po.databinding.ActivityMainBinding
import com.dean.pr_po.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var settingsBinding: ActivitySettingsBinding
    private lateinit var mUserPreference: UserPreference
    private var userData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        mUserPreference = UserPreference(this)

        var dataPreference = UserData()
        dataPreference = mUserPreference.getUser()
        settingsBinding.valueUser.text = dataPreference.username
        settingsBinding.valueIdUser.text = dataPreference.pId_user
        settingsBinding.valueHost.text = dataPreference.pAshost


        settingsBinding.btnLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val builder = AlertDialog.Builder(this@SettingsActivity)
        builder.setTitle("Informasi")
        builder.setIcon(R.drawable.information)
        builder.setMessage("Apakah anda yakin untuk keluar.?")
        builder.setCancelable(false)
        builder.setPositiveButton("Ya") { dialog, which ->
            mUserPreference.deleteUser(userData)
            startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
        }
        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }
}