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

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME_OUT:Long = 3000
    lateinit var pConfig: GlobalConfig

    companion object {
        val TAG = SplashActivity::class.java.simpleName
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileEvents = File(GlobalConfig.pInitAppl)
        if(fileEvents.exists())
            binding.textViewApp.text = GlobalConfig.pVer + "." + GlobalConfig.pDev
        else{
            Toast.makeText(this@SplashActivity, "File INITAPPL Tidak ditemukan, Hubungi segera Administrator..!", Toast.LENGTH_SHORT).show()
        }
        /*val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(fileEvents))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                GlobalConfig.setBaseURL(GlobalConfig(),text.toString())
            }
            br.close()
        } catch (e: IOException) {
        }
        if(GlobalConfig.baseURL!= null){
            pConfig.pIp_webser = pConfig.baseURL
            binding.textViewApp.text = pConfig.pVer + "." + pConfig.pDev
        }else{
            Toast.makeText(this@SplashActivity, "File INITAPPL Tidak ditemukan, Hubungi segera Administrator..!", Toast.LENGTH_SHORT).show()
        }*/


        Handler(Looper.getMainLooper()).postDelayed({
            getVersion()
        }, SPLASH_TIME_OUT)

    }

    private fun getVersion(){
        binding.progressBar.visibility = View.VISIBLE
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

                binding.progressBar.visibility = View.INVISIBLE
                val result = String (responseBody)
                Log.d(SplashActivity.TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")

                    for (i in 0 until returnMessage.length()){
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
                            GlobalConfig.pAppname = responseLogin.getString("appname")
                            GlobalConfig.pVer = responseLogin.getString("version")
                            GlobalConfig.pDev = responseLogin.getString("dev")

                            if (sVerCode.equals(GlobalConfig.pVer) && versionName.equals(GlobalConfig.pDev)){
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
                binding.progressBar.visibility = View.INVISIBLE
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