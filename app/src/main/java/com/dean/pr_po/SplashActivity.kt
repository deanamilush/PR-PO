package com.dean.pr_po

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dean.pr_po.databinding.ActivitySplashBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class SplashActivity : AppCompatActivity() {

    companion object {
        val TAG = SplashActivity::class.java.simpleName
        const val pDATA = "extra_data"
    }

    private lateinit var splashBinding: ActivitySplashBinding
    private val SPLASH_TIME_OUT:Long = 3000
    private lateinit var mUserPreference: UserPreference
    private var userData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        mUserPreference = UserPreference(this)
        userData = mUserPreference.getUser()
//        val pData = intent.getParcelableExtra<UserData>(pDATA) as UserData

        Handler(Looper.getMainLooper()).postDelayed({
            getVersion()

            /*if (pData != null){
                val gotomain = Intent(this@SplashActivity, MainActivity::class.java)
                gotomain.putExtra(MainActivity.pDATA, pData)
                startActivity(gotomain)
            } else{
                Toast.makeText(this@SplashActivity, "anda harus login", Toast.LENGTH_SHORT)
                    .show()
            }*/
        }, SPLASH_TIME_OUT)

    }

    private fun getVersion(){
        splashBinding.progressBar.visibility = View.VISIBLE
        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val sVerCode  = versionCode.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        val url = GlobalConfig.urlVersion
        client.post(url, params, object: AsyncHttpResponseHandler(){
            @SuppressLint("SetTextI18n")
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                splashBinding.progressBar.visibility = View.INVISIBLE
                val result = String (responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")

                    for (i in 0 until returnMessage.length()){
                        val dataUser = UserData()
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")){
                            val builder = AlertDialog.Builder(this@SplashActivity)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        } else {

                            val resultMessage = responseObject.getJSONArray("result")
                            val responseLogin = resultMessage.getJSONObject(0)
                            dataUser.version = responseLogin.getString("version")
                            dataUser.dev = responseLogin.getString("dev")

                            if (sVerCode.equals(dataUser.version) && versionName.equals(dataUser.dev)){
                                splashBinding.tvVersion.text = dataUser.version + "." + dataUser.dev
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                    finish()
                                }, SPLASH_TIME_OUT)
                            } else{
                                val builder = AlertDialog.Builder(this@SplashActivity)
                                builder.setTitle("Error")
                                builder.setMessage("Segera Perbaharui Aplikasi versi Terbaru..!")
                                builder.setCancelable(false)
                                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                    dialog.cancel()
                                }
                                builder.show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SplashActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray, error: Throwable) {
                splashBinding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@SplashActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getLog() {
        val pData = intent.getParcelableExtra<UserData>(MainActivity.pDATA) as? UserData
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_user", pData?.pId_user)
        params.put("id_app", GlobalConfig.pId_app)
        params.put("id_conn", pData?.pId_conn)
        val url = "http://192.168.1.8/GlobalInc/verifLog.php"
        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")

                    for (i in 0 until returnMessage.length()) {
                        val dataUser = UserData()
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")) {
                            val builder = AlertDialog.Builder(this@SplashActivity)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        } else {
                            val resultMessage = responseObject.getJSONArray("result")
                            val responseLogin = resultMessage.getJSONObject(0)
                            dataUser.pPlant = responseLogin.getString("plant")
                            dataUser.username = responseLogin.getString("username")
                            dataUser.password = responseLogin.getString("pass")
                            dataUser.pUser_sap = responseLogin.getString("user_sap")
                            dataUser.pPass_sap = responseLogin.getString("password")
                            dataUser.pAshost = responseLogin.getString("ashost")
                            dataUser.pSysnr = responseLogin.getString("sysnr")
                            dataUser.pClient = responseLogin.getString("client")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SplashActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable
                ) {
                    val errorMessage = when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        else -> "$statusCode : ${error.message}"
                    }
                    Toast.makeText(this@SplashActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
        })
    }
}