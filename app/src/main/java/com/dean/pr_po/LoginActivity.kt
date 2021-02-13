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
    private var pConfig :Config? = null
    val arrayList = ArrayList<UserData>()

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
      //  client.addHeader("id_app", idApp)
        client.get(url, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

         //       val result = String (responseBody)
            //    Log.d(TAG, result)

                try {

                    val responseObject = JSONObject()
                    val returnMessage = responseObject.getJSONArray("return")
                    val jsonObject = returnMessage.getJSONObject(0)
                    val messageErrorLogin : String = jsonObject.getString("type")
                    if (messageErrorLogin.equals("E")){
                        val builder = AlertDialog.Builder(this@LoginActivity)
                        builder.setTitle("Error")
                        builder.setMessage(messageErrorLogin)
                        builder.setCancelable(false)
                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            dialog.cancel()
                        }
                        builder.show()
                    }


                    /*for (i in 0 until returnMessage.length()) {
                        val jsonObject = returnMessage.getJSONObject(i)
                        val messageErrorLogin : String = jsonObject.getString("msg")

                        if (messageErrorLogin.equals("E")){
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        }else{
                            val responseObjectUser = JSONObject()
                            val resultMessage = responseObject.getJSONArray("result")

                            for (j in 0 until returnMessage.length()) {
                                val jsonObjectUser = returnMessage.getJSONObject(j)
                                val username: String = jsonObjectUser.getString("username")
                                val password: String = jsonObjectUser.getString("password")
                                arrayList.add(UserData(username, password))
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }

                        }


                    }*/
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



    private fun loadAllStudents(){

        val loading = ProgressDialog(this)
        loading.setMessage("Memuat data...")
        loading.show()

        AndroidNetworking.get(GlobalConfig.loginUser)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {

                    override fun onResponse(response: JSONObject?) {

                        val jsonArray = response?.optJSONArray("result")

                        if(jsonArray?.length() == 0){
                            loading.dismiss()
                            Toast.makeText(applicationContext,"Student data is empty, Add the data first",Toast.LENGTH_SHORT).show()
                        }

                        for(i in 0 until jsonArray?.length()!!){

                            val jsonObject = jsonArray?.optJSONObject(i)
                            arrayList.add(UserData(jsonObject.getString("username"),
                                    jsonObject.getString("password")))
                            val intent = Intent(this@LoginActivity, LoginActivity::class.java)
                            startActivity(intent)

                          /*  if(jsonArray?.length() - 1 == i){

                                loading.dismiss()
                                val adapter = RVAAdapterStudent(applicationContext,arrayList)
                                adapter.notifyDataSetChanged()
                                mRecyclerView.adapter = adapter

                            }*/

                        }

                    }

                    override fun onError(anError: ANError?) {
                        loading.dismiss()
                        Toast.makeText(applicationContext,"Connection Failure",Toast.LENGTH_SHORT).show()
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
}