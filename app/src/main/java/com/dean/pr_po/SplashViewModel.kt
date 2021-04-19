package com.dean.pr_po

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class SplashViewModel : ViewModel() {

    val listPic = MutableLiveData<ArrayList<UserData>>()

    fun setDataUser(context: Context) {
        // request API
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
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        } else {
                            val userItems = UserData()
                            val resultMessage = responseObject.getJSONArray("result")
                            val responseLogin = resultMessage.getJSONObject(0)
                            val appname = responseLogin.getString("appname")
                            userItems.version = responseLogin.getString("version")
                            userItems.dev = responseLogin.getString("dev")

                            if (sVerCode.equals(userItems.version) && versionName.equals(userItems.dev)){
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Error")
                                builder.setMessage("Berhasil cek version!!!!")
                                builder.setCancelable(false)
                                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                    dialog.cancel()
                                }
                                builder.show()
                                /*Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(context, LoginActivity::class.java)
                                    startActivity(intent)
                                },3000)*/
                            } else{
                                val builder = AlertDialog.Builder(context)
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
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun getDataUser(): LiveData<ArrayList<UserData>> {
        return listPic
    }
}