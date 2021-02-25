package com.dean.pr_po

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

     //   getSeasonUser()
        binding.progressBar.visibility = View.INVISIBLE

        binding.btnLogin.setOnClickListener(this)
    }

    private fun getUserLogin(){
        binding.progressBar.visibility = View.VISIBLE
        val loginUser = binding.valueLogin.text.toString()
        val loginPass = binding.valuePassword.text.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        params.put("username", "admin")
        params.put("password", "1234")
        val url = GlobalConfig.urlLogin
        client.post(url, params, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                binding.progressBar.visibility = View.INVISIBLE
                val result = String (responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    for (i in 0 until returnMessage.length()){
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")){
                            binding.progressBar.visibility = View.INVISIBLE
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        } else {
                            // get username and password from webservice
                            val resultMessage = responseObject.getJSONArray("result")
                            val responseLogin = resultMessage.getJSONObject(0)
                            val username = responseLogin.getString("username")
                            val password = responseLogin.getString("password")

                            //validasi username
                            if (username.equals(loginUser) && password.equals(loginPass)){
                                binding.progressBar.visibility = View.INVISIBLE
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else /*if (username != loginUser && password != loginPass)*/{
                                binding.progressBar.visibility = View.INVISIBLE
                                val builder = AlertDialog.Builder(this@LoginActivity)
                                builder.setTitle("Error")
                                builder.setIcon(R.drawable.warning)
                                builder.setMessage("Username / Password Salah")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Coba Lagi") { dialog, which ->
                                    dialog.cancel()
                                }
                                builder.show()
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
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                val builder = AlertDialog.Builder(this@LoginActivity)
                builder.setTitle("Error")
                builder.setIcon(R.drawable.warning)
                builder.setMessage(errorMessage)
                builder.setCancelable(false)
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialog.cancel()
                }
                builder.show()
            }

        })
    }

    private fun getSeasonUser(){
        if (AppPreferences.isLogin) {
            AppPreferences.isLogin = false
            AppPreferences.username = ""
            AppPreferences.password = ""
        } else {
            val username = binding.valueLogin.text.toString()
            val password = binding.valuePassword.text.toString()
            if (username.isNotBlank() && password.isNotBlank()) {
                AppPreferences.isLogin = true
                AppPreferences.username = username
                AppPreferences.password = password
            } else {
                Toast.makeText(this, "login validation", Toast.LENGTH_SHORT).show()
            }
        }
        //setupLoginLayout()
    }

    private fun setupLoginLayout() {
        if (AppPreferences.isLogin) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            val builder = AlertDialog.Builder(this@LoginActivity)
            builder.setTitle("Error")
            builder.setIcon(R.drawable.warning)
            builder.setMessage("Login terlebih dahulu")
            builder.setCancelable(false)
            builder.setPositiveButton("OK") { dialog, which ->
                dialog.cancel()
            }
            builder.show()
        }
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