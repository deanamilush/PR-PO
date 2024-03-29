package com.dean.pr_po

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dean.pr_po.databinding.ActivityUserDataBinding
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class UserDataActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var userDataBinding: ActivityUserDataBinding
    private val list = ArrayList<UserData>()
    private val nameAdapter = NameAdapter(list)
    private var userData = UserData()
    private lateinit var mUserPreference: UserPreference
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDataBinding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(userDataBinding.root)

        mUserPreference = UserPreference(this)
        swipeRefreshLayout = findViewById(R.id.swipeContainer)

        userDataBinding.recyclerView.setHasFixedSize(true)
        userDataBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                userDataBinding.recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        userDataBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        userDataBinding.recyclerView.adapter = nameAdapter

        nameAdapter.setOnItemClickCallback(object : NameAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserData) {
                selectDetail(data)
            }
        })

        swipeRefreshLayout.setOnRefreshListener(){
            list.clear()
            val dataPreference= mUserPreference.getUser()
            val client = AsyncHttpClient()
            val DEFAULT_TIMEOUT = 40 * 1000
            client.setTimeout(DEFAULT_TIMEOUT)
            val params = RequestParams()
            params.put("ashost", dataPreference.pAshost)
            params.put("sysnr", dataPreference.pSysnr)
            params.put("client", dataPreference.pClient)
            params.put("usap", dataPreference.pUser_sap)
            params.put("psap", dataPreference.pPass_sap)
            params.put("id_user", dataPreference.pId_user)
            val url = "http://developer.gsg.co.id/prpo/api/rpt/valprpo"
            client.post(url, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseBody: ByteArray
                ) {
                    val result = String(responseBody)
                    Log.d(TAG, result)
                    try {
                        val responseObject = JSONObject(result)
                        val returnMessage = responseObject.getJSONArray("return")
                        val tPurc = responseObject.getJSONArray("t_purc")
                        val tPr = responseObject.getJSONArray("t_pr")
                        val tPo = responseObject.getJSONArray("t_po")
                        for (i in 0 until returnMessage.length()) {
                            val jsonObject = returnMessage.getJSONObject(i)
                            val typeErrorLogin = jsonObject.getString("type")
                            val messageErrorLogin = jsonObject.getString("msg")
                            if (typeErrorLogin.equals("E")) {
                                val builder = AlertDialog.Builder(this@UserDataActivity)
                                builder.setTitle("Error")
                                builder.setIcon(R.drawable.warning)
                                builder.setMessage(messageErrorLogin)
                                builder.setCancelable(false)
                                builder.setPositiveButton("OK") { dialog, which ->
                                    dialog.cancel()
                                }
                                builder.show()
                            } else {
                                for (l in 0 until tPurc.length()) {
                                    val user = tPurc.getJSONObject(l)
                                    val userData = UserData()
                                    userData.name = user.getString("BEDNR")
                                    val lBednr = user.getString("BEDNR")

                                    for (j in 0 until tPr.length()) {
                                        val dataPr = tPr.getJSONObject(j)
                                        if (lBednr.equals(dataPr.getString("BEDNR"))) {
                                            userData.prThisMonth = dataPr.getInt("QCUR_MT")
                                            userData.prLastMonth = dataPr.getInt("QPREV_MT")
                                            userData.prMonthAgo = dataPr.getInt("QLAST_MT")
                                            break
                                        }
                                    }
                                    for (k in 0 until tPo.length()) {
                                        val dataPo = tPo.getJSONObject(k)
                                        if (lBednr.equals(dataPo.getString("BEDNR"))) {
                                            userData.poThisMonth = dataPo.getInt("QCUR_MT")
                                            userData.poLastMonth = dataPo.getInt("QPREV_MT")
                                            userData.poMonthAgo = dataPo.getInt("QLAST_MT")
                                            break
                                        }
                                    }

                                    list.add(userData)
                                    nameAdapter.notifyDataSetChanged()
                                    showSnackbarMessage("Data Berhasil di Update")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@UserDataActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray,
                    error: Throwable
                ) {
                    val errorMessage = when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        else -> "$statusCode : ${error.message}"
                    }

                    val mError = errorMessage.substring(0, 22)
                    val builder = AlertDialog.Builder(this@UserDataActivity)
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

            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 6000)
        }


        getListUser()

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_red_light
        )
    }

    private fun selectDetail(dataUser: UserData) {
        val gotoSplash = Intent(this@UserDataActivity, DetailActivity::class.java)
        gotoSplash.putExtra(DetailActivity.EXTRA_DATA, dataUser)
        startActivity(gotoSplash)
    }

    private fun getListUser(){
        showLoading(true)
        val dataPreference= mUserPreference.getUser()
        val client = AsyncHttpClient()
        val DEFAULT_TIMEOUT = 40 * 1000
        client.setTimeout(DEFAULT_TIMEOUT)
        val params = RequestParams()
        params.put("ashost", dataPreference.pAshost)
        params.put("sysnr", dataPreference.pSysnr)
        params.put("client", dataPreference.pClient)
        params.put("usap", dataPreference.pUser_sap)
        params.put("psap", dataPreference.pPass_sap)
        params.put("id_user", dataPreference.pId_user)
        val url = "http://developer.gsg.co.id/prpo/api/rpt/valprpo"
        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                showLoading(false)
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val returnMessage = responseObject.getJSONArray("return")
                    val tPurc = responseObject.getJSONArray("t_purc")
                    val tPr = responseObject.getJSONArray("t_pr")
                    val tPo = responseObject.getJSONArray("t_po")
                    for (i in 0 until returnMessage.length()) {
                        val jsonObject = returnMessage.getJSONObject(i)
                        val typeErrorLogin = jsonObject.getString("type")
                        val messageErrorLogin = jsonObject.getString("msg")
                        if (typeErrorLogin.equals("E")) {
                            showLoading(false)
                            val builder = AlertDialog.Builder(this@UserDataActivity)
                            builder.setTitle("Error")
                            builder.setIcon(R.drawable.warning)
                            builder.setMessage(messageErrorLogin)
                            builder.setCancelable(false)
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        } else {
                            for (l in 0 until tPurc.length()) {
                                val user = tPurc.getJSONObject(l)
                                val userData = UserData()
                                userData.name = user.getString("BEDNR")
                                val lBednr = user.getString("BEDNR")

                                for (j in 0 until tPr.length()) {
                                    val dataPr = tPr.getJSONObject(j)
                                    if (lBednr.equals(dataPr.getString("BEDNR"))) {
                                        userData.prThisMonth = dataPr.getInt("QCUR_MT")
                                        userData.prLastMonth = dataPr.getInt("QPREV_MT")
                                        userData.prMonthAgo = dataPr.getInt("QLAST_MT")
                                        break
                                    }
                                }
                                for (k in 0 until tPo.length()) {
                                    val dataPo = tPo.getJSONObject(k)
                                    if (lBednr.equals(dataPo.getString("BEDNR"))) {
                                        userData.poThisMonth = dataPo.getInt("QCUR_MT")
                                        userData.poLastMonth = dataPo.getInt("QPREV_MT")
                                        userData.poMonthAgo = dataPo.getInt("QLAST_MT")
                                        break
                                    }
                                }

                                list.add(userData)
                                nameAdapter.notifyDataSetChanged()

                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@UserDataActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray,
                error: Throwable
            ) {
                showLoading(false)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                val mError = errorMessage.substring(0, 22)
                val builder = AlertDialog.Builder(this@UserDataActivity)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val gotoSplash = Intent(this@UserDataActivity, SettingsActivity::class.java)
            startActivity(gotoSplash)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            userDataBinding.progressBar.visibility = View.VISIBLE
        } else {
            userDataBinding.progressBar.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val toExit = Intent(this@UserDataActivity, MainActivity::class.java)
        startActivity(toExit)
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(userDataBinding.recyclerView, message, Snackbar.LENGTH_SHORT).show()
    }
}