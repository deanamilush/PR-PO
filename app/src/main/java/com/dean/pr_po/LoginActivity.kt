package com.dean.pr_po

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dean.pr_po.databinding.ActivityLoginBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private const val FIELD_REQUIRED = "Field tidak boleh kosong"
    }
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var mUserPreference: UserPreference
    private lateinit var loadingDialog: ProgressDialog
    private var userData = UserData()
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loadingDialog = ProgressDialog(this)

        loginBinding.btnLogin.setOnClickListener(this)

        mUserPreference = UserPreference(this)
        showExistingPreference()

    }

    private fun showExistingPreference() {
       val dataPreference = mUserPreference.getUser()
        if(dataPreference.username.equals("")){
            loginBinding.valueLogin.requestFocus()
        }else{
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private fun getUserLogin(){
        val loginUser = loginBinding.valueLogin.text.toString()
        val loginPass = loginBinding.valuePassword.text.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_app", GlobalConfig.pId_app)
        params.put("username", loginUser)
        params.put("password", loginPass)
        val url = GlobalConfig.urlLogin
        client.post(url, params, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                dismissDialog()
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
                            /*val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("Error")
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()*/
                        } else {
                            // get username and password from webservice
                            val responseLogin = resultMessage.getJSONObject(0)
                            userData.pId_user = responseLogin.getString("id_user")
                            userData.username = responseLogin.getString("username")
                            userData.pId_conn = responseLogin.getString("id_conn")
                            userData.pPlant = responseLogin.getString("plant")
                            userData.pUser_sap = responseLogin.getString("user_sap")
                            userData.pSysnr = responseLogin.getString("sysnr")
                            userData.pAshost = responseLogin.getString("ashost")
                            userData.pClient = responseLogin.getString("client")
                            userData.pPass_sap = responseLogin.getString("password")


                        }
                    }
                    startLoadingDialog()
                    getLog()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray, error: Throwable) {
                dismissDialog()
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                val mError = errorMessage.substring(0, 22)
                val builder = AlertDialog.Builder(this@LoginActivity)
                builder.setTitle("Error")
                builder.setIcon(R.drawable.warning)
                builder.setMessage(mError)
                builder.setCancelable(false)
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialog.cancel()
                }
                builder.show()
            }

        })
    }

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
        dialog.cancel()
    }

    private fun getLog() {
        val loginUser = loginBinding.valueLogin.text.toString()
        val loginPass = loginBinding.valuePassword.text.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("id_user", userData.pId_user)
        params.put("id_app", GlobalConfig.pId_app)
        params.put("id_conn", userData.pId_conn)
        val url = GlobalConfig.urlVerifLog
        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray
            ) {
                dismissDialog()
                val result = String(responseBody)
                Log.d(SplashActivity.TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    val resultMessage = responseObject.getJSONArray("result")

                    for (i in 0 until returnMessage.length()) {
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")) {
                            dismissDialog()
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("Error")
                            builder.setIcon(R.drawable.warning)
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                dismissDialog()
                                dialog.cancel()
                            }
                            builder.show()
                        } else {

                            val responseLogin = resultMessage.getJSONObject(0)
                            userData.pPlant = responseLogin.getString("plant")
                            userData.username = responseLogin.getString("username")
                            userData.password = responseLogin.getString("pass")
                            userData.pUser_sap = responseLogin.getString("user_sap")
                            userData.pPass_sap = responseLogin.getString("password")
                            userData.pAshost = responseLogin.getString("ashost")
                            userData.pSysnr = responseLogin.getString("sysnr")
                            userData.pClient = responseLogin.getString("client")

                            if (userData.username.equals(loginUser) && userData.password.equals(loginPass)){
                                saveUser(loginUser, loginPass, userData.pUser_sap, userData.pPass_sap, userData.pAshost, userData.pSysnr, userData.pClient, userData.pId_user)
                                val gotomain = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(gotomain)

                            } else {
                                val builder = AlertDialog.Builder(this@LoginActivity)
                                builder.setTitle("Error")
                                builder.setIcon(R.drawable.warning)
                                builder.setMessage("Username / Password Salah")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Coba Lagi") { dialog, which ->
                                    dismissDialog()
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

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable
            ) {
                dismissDialog()
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                val mError = errorMessage.substring(0, 22)
                val builder = AlertDialog.Builder(this@LoginActivity)
                builder.setTitle("Error")
                builder.setIcon(R.drawable.warning)
                builder.setMessage(mError)
                builder.setCancelable(false)
                builder.setPositiveButton("OK") { dialog, which ->
                    dismissDialog()
                    dialog.cancel()
                }
                builder.show()
            }
        })
    }

    override fun onClick(p0: View?) {

        startLoadingDialog()
        val username = loginBinding.valueLogin.text.toString()
        val password = loginBinding.valuePassword.text.toString()

        if (username.isEmpty()){
            dismissDialog()
            loginBinding.valueLogin.error = FIELD_REQUIRED
            return
        } else if (password.isEmpty()){
            dismissDialog()
            loginBinding.valuePassword.error = FIELD_REQUIRED
            return
        } else{
            getUserLogin()
        }
    }

    private fun saveUser(username: String, password: String, userSap: String, passSap: String, ashost: String, sysnr: String, client: String, idUser: String) {
        val userPreference = UserPreference(this)
        userData.username = username
        userData.password = password
        userData.pUser_sap = userSap
        userData.pPass_sap = passSap
        userData.pAshost = ashost
        userData.pSysnr = sysnr
        userData.pClient = client
        userData.pId_user = idUser
        userPreference.setUser(userData)
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