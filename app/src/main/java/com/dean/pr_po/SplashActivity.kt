package com.dean.pr_po

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dean.pr_po.databinding.ActivitySplashBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME_OUT:Long = 3000

    companion object {
        private val TAG = SplashActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            getVersion()
            /*startActivity(Intent(this,MainActivity::class.java))
            finish()*/
        }, SPLASH_TIME_OUT)
    }

    private fun getVersion(){
        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val sVerCode  = versionCode.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        val url = "http://192.168.1.8/GlobalInc/verService.php"
        val idApp = GlobalConfig.pId_app
        client.post(url, params, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
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
                            val appname = responseLogin.getString("appname")
                            val version = responseLogin.getString("version")
                            val dev = responseLogin.getString("dev")

                            binding.tvVersion.text = version

                            if (sVerCode.equals(version) && versionName.equals(dev)){
                                val move = Intent(this@SplashActivity, LoginActivity::class.java)
                                startActivity(move)
                            } else{
                                Toast.makeText(this@SplashActivity, "Segera Perbaharui Aplikasi versi Terbaru..!", Toast.LENGTH_SHORT)
                                    .show()
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