package com.dean.pr_po

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dean.pr_po.databinding.ActivityLoginBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private const val FIELD_REQUIRED = "Field tidak boleh kosong"
    }
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var mUserPreference: UserPreference
    private var userData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.progressBar.visibility = View.INVISIBLE

        loginBinding.btnLogin.setOnClickListener(this)

        mUserPreference = UserPreference(this)
        showExistingPreference()

    }

    private fun showExistingPreference() {
        userData = mUserPreference.getUser()
        if (userData.username?.isEmpty() == true){
            loginBinding.valueLogin.text = null
            loginBinding.valuePassword.text = null
        }else{
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private fun getUserLogin(){
        loginBinding.progressBar.visibility = View.VISIBLE
        val loginUser = loginBinding.valueLogin.text.toString()
        val loginPass = loginBinding.valuePassword.text.toString()
        val pUser = BuildConfig.PARAMS_USERNAME
        val pPass = BuildConfig.PARAMS_PASSWORD
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        params.put("username", pUser)
        params.put("password", pPass)
        val url = GlobalConfig.urlLogin
        client.post(url, params, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                loginBinding.progressBar.visibility = View.INVISIBLE
                val result = String (responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    for (i in 0 until returnMessage.length()){
                        val pUserData = UserData()
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")){
                            loginBinding.progressBar.visibility = View.INVISIBLE
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
                            pUserData.username = responseLogin.getString("username")
                            pUserData.password = responseLogin.getString("password")
                            pUserData.pId_user = responseLogin.getString("id_user")
                            pUserData.pId_conn = responseLogin.getString("id_conn")
                            pUserData.pPlant = responseLogin.getString("plant")
                            pUserData.pUser_sap = responseLogin.getString("user_sap")
                            pUserData.pSysnr = responseLogin.getString("sysnr")
                            pUserData.pAshost = responseLogin.getString("ashost")
                            pUserData.pClient = responseLogin.getString("client")
                            pUserData.pPass_sap = responseLogin.getString("password")
                            //validasi username
                            if (pUserData.username.equals(loginUser) && pPass.equals(loginPass)){
                                loginBinding.progressBar.visibility = View.INVISIBLE
                                val gotomain = Intent(this@LoginActivity, MainActivity::class.java)
                                gotomain.putExtra(MainActivity.pDATA, pUserData)
                                gotomain.putExtra(SplashActivity.pDATA, pUserData)
                                startActivity(gotomain)

                                val values = ContentValues()
                                values.put(TlogContract.TlogColumns.ID_USER, pUserData.pId_user)
                                values.put(TlogContract.TlogColumns.ID_CONN, pUserData.pId_conn)
                                values.put(TlogContract.TlogColumns.ID_APP, GlobalConfig.pId_app)

                                finish()
                            } else {
                                loginBinding.progressBar.visibility = View.INVISIBLE
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
                loginBinding.progressBar.visibility = View.INVISIBLE
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

    override fun onClick(p0: View?) {

        val username = loginBinding.valueLogin.text.toString()
        val password = loginBinding.valuePassword.text.toString()

        if (username.isEmpty()){
            loginBinding.valueLogin.error = FIELD_REQUIRED
            return
        } else if (password.isEmpty()){
            loginBinding.valuePassword.error = FIELD_REQUIRED
            return
        } else{
            getUserLogin()
        }
        saveUser(username, password)
    }

    private fun saveUser(username: String, password: String) {
        val userPreference = UserPreference(this)

        userData.username = username
        userData.password = password

        userPreference.setUser(userData)
        Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toExit = Intent(Intent.ACTION_MAIN)
        toExit.addCategory(Intent.CATEGORY_HOME)
        toExit.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(toExit)
        finish()
    }
}