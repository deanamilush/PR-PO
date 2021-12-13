package com.dean.pr_po

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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

class SplashActivity : AppCompatActivity() {

    companion object {
        val TAG = SplashActivity::class.java.simpleName
    }

    private lateinit var splashBinding: ActivitySplashBinding
    private val SPLASH_TIME_OUT:Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            getVersion()
        }, SPLASH_TIME_OUT)

        }

    private fun getVersion(){
        splashBinding.progressBar.visibility = View.VISIBLE
        val verApp = BuildConfig.VERSION_NAME
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        val url = "http://dev.gsg.co.id/prpo/api/log/verserv"
        client.post(url, params, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray?) {
                splashBinding.progressBar.visibility = View.INVISIBLE
                val result = String (responseBody!!)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    val resultMessage = responseObject.getJSONArray("result")
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
                            val responseLogin = resultMessage.getJSONObject(0)
                            dataUser.version = responseLogin.getString("version")
                            dataUser.dev = responseLogin.getString("dev")
                            val dbvers = dataUser.version.toString() + "." + dataUser.dev

                            if (verApp.equals(dbvers)){
                                splashBinding.tvVersion.text = verApp
                                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            } else{
                                val builder = AlertDialog.Builder(this@SplashActivity)
                                builder.setTitle("Error")
                                builder.setMessage("Harap Perbaharui Versi Aplikasi")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Update") { dialog, which ->
                                    try {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("market://details?id=" + this@SplashActivity.packageName)
                                            )
                                        )
                                    } catch (e: ActivityNotFoundException) {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id=" + this@SplashActivity.packageName)
                                            )
                                        )
                                    }
                                    finish()
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

            override fun onFailure(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray, error: Throwable) {
                splashBinding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                Toast.makeText(this@SplashActivity, errorMessage, Toast.LENGTH_SHORT)
                        .show()
                val mError = errorMessage.substring(0, 22)
                val builder = AlertDialog.Builder(this@SplashActivity)
                builder.setTitle("Error")
                builder.setMessage(mError)
                builder.setCancelable(false)
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.cancel()
                }
                builder.show()
            }
        })
    }
}