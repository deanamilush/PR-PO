package com.dean.pr_po

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dean.pr_po.GlobalConfig.Companion.password
import com.dean.pr_po.GlobalConfig.Companion.username
import com.dean.pr_po.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private val EXTRA_PASSWORD = "password"
        private val EXTRA_USER = "username"
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
    }

    private fun getUserLogin(){
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        val url = "http://192.168.1.8/GlobalInc/loginService.php"
        val idApp = GlobalConfig.pId_app
        client.post(url, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String (responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    val resultMessage = responseObject.getJSONArray("result")
                    for (i in 0 until returnMessage.length()){
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")){
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        } else {
                            for (j in 0 until resultMessage.length()){
                                val responseLogin = resultMessage.getJSONObject(j)
                                val username = responseLogin.getString("username")
                                val password = responseLogin.getString("password")
                                val idApp = responseLogin.getString("id_app")

                                binding.tvUsername.text = username
                                binding.tvPassword.text = password

                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
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
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun onClick(p0: View?) {

        val username = binding.valueLogin.text.toString()
        val password = binding.valuePassword.text.toString()

        if (username.isEmpty()){
            binding.valueLogin.setError("Tidak Boleh Kosong")
        } else if (password.isEmpty()){
            binding.valuePassword.setError("Tidak Boleh Kosong")
        } else  {
            getUserLogin()
        }
    }
}