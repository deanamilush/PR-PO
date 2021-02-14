package com.dean.pr_po

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.dean.pr_po.databinding.ActivityLoginBinding
import com.dean.pr_po.ui.pr.UserData
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val objCon = intent.getStringExtra("pConfig")

        binding.btnLogin.setOnClickListener(this)
    }

    private fun getUserLogin(){
        val client = AsyncHttpClient()
        val url = "http://192.168.1.8/GlobalInc/loginService.php"
        val idApp = GlobalConfig.pId_app
        client.post(url, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                val result = String (responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    val jsonObject = returnMessage.getJSONObject(0)
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
                    }else{
                        val resultMessage = responseObject.getJSONArray("result")
                        val responseLogin = resultMessage.getJSONObject(0)
                        val username = responseLogin.getString("username")
                        val password = responseLogin.getString("password")

                        responseObject.put(username, binding.valueLogin.text.toString())
                        responseObject.put(password, binding.valuePassword.text.toString())

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
        } else {
           getUserLogin()
           // binding.progressBar.visibility = View.VISIBLE
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
        }
    }

    /*private fun verifyFromSQLite() {

        if (!inputValidation!!.isInputEditTextFilled(
                textInputEditTextEmail!!,
                textInputLayoutEmail!!,
                getString(R.string.error_message_email))) {
            return
        } else if (!inputValidation!!.isInputEditTextFilled(
                textInputEditTextPassword!!,
                textInputLayoutPassword!!,
                getString(R.string.error_message_password))) {
            return
        } else {
            val json = JSONObject()
            json.put("email", textInputEditTextEmail.text.toString())
            json.put("password", textInputEditTextPassword.text.toString())

            progressBar.visibility = View.VISIBLE            HttpTask({                progressBar.visibility = View.INVISIBLE                if (it == null) {
                println("connection error")
                return@HttpTask                }
                println(it)
                val json_res = JSONObject(it)
                if (json_res.getString("status").equals("true")) {
                    var userdata = User()
                    var jsonArray = JSONArray(json_res.getString("data"))
                    for (i in 0..(jsonArray.length() - 1)) {
                        val item = jsonArray.getJSONObject(i)
                        userdata.id = item.getString("id")
                        userdata.username = item.getString("username")
                        userdata.email = item.getString("email")
                    }
                    emptyInputEditText()
                    val intent = Intent(activity, HomeActivity::class.java)
                    intent.putExtra("id", userdata.id)
                    startActivity(intent)
                    Log.d("userdata Data:::::::", userdata.toString())
                } else {
                    Log.d("psot Data:::::::", json_res.getString("message"))
                    Snackbar.make(nestedScrollView!!, json_res.getString("message"), Snackbar.LENGTH_LONG).show()
                }

            }).execute("POST", "http://192.168.1.111/KotlinExample/LoginRegistration/login.php", json.toString())
        }
    }

    private fun emptyInputEditText() {
        textInputEditTextEmail!!.text = null        textInputEditTextPassword!!.text = null    }*/

}