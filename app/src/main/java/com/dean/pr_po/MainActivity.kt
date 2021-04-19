package com.dean.pr_po

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dean.pr_po.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        const val pDATA = "extra_data"
        const val pCurrent = "extra_current"
        private val TAG = MainActivity::class.java.simpleName
        private const val JOB_ID = 10
    }

    private lateinit var mainBinding: ActivityMainBinding
    private val list = ArrayList<UserData>()
    private val adapter = ListAdapter(list)
    private var userData = UserData()
    private lateinit var mUserPreference: UserPreference
    private lateinit var getCurrentData: GetCurrentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mUserPreference = UserPreference(this)

        mainBinding.recyclerView.setHasFixedSize(true)
        mainBinding.recyclerView.addItemDecoration(DividerItemDecoration(mainBinding.recyclerView.context, DividerItemDecoration.VERTICAL))
        mainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.adapter = adapter



        startJob()
    }

    private fun configViewModel(adapter: ListAdapter) {
        getCurrentData.getListUsers().observe(this, Observer { listUsers ->
            if (listUsers != null) {
                adapter.setData(listUsers)
                showLoading(false)
            }
        })
    }

    private fun getListUser(){
        mainBinding.progressBar.visibility = View.VISIBLE
        val pData = intent.getParcelableExtra<UserData>(pDATA) as? UserData
        val client = AsyncHttpClient()
        val DEFAULT_TIMEOUT = 40 * 1000
        client.setTimeout(DEFAULT_TIMEOUT)
        val params = RequestParams()
        params.put("ashost", pData?.pAshost)
        params.put("sysnr", pData?.pSysnr)
        params.put("client", pData?.pClient)
        params.put("usap", pData?.pUser_sap)
        params.put("psap", pData?.pPass_sap)
        val url = "http://192.168.1.8/GlobalInc/valPrPo.php"
        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                mainBinding.progressBar.visibility = View.INVISIBLE
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
                            mainBinding.progressBar.visibility = View.INVISIBLE
                            val builder = AlertDialog.Builder(this@MainActivity)
                            builder.setTitle("Error")
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
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray, error: Throwable) {
                mainBinding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                val builder = AlertDialog.Builder(this@MainActivity)
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

    private fun startJob(){
        getListUser()
        showLoading(false)
        val mServiceComponent = ComponentName(this, GetCurrentData::class.java)
        val pData = intent.getParcelableExtra<UserData>(pDATA) as? UserData
        val builder = JobInfo.Builder(JOB_ID, mServiceComponent)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        builder.setRequiresDeviceIdle(false)
        builder.setRequiresCharging(false)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(900000) //15 menit
        } else {
            builder.setPeriodic(180000) //3 menit
        }

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(builder.build())
        Toast.makeText(this, "Job Service started", Toast.LENGTH_SHORT).show()
    }

    private fun actionLogout() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Informasi")
        builder.setIcon(R.drawable.warning)
        builder.setMessage("Anda Yakin ingin Logout")
        builder.setCancelable(false)
        builder.setPositiveButton("Ya") { dialog, which ->
            mUserPreference.deleteUser(userData)
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        builder.show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            actionLogout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            mainBinding.progressBar.visibility = View.VISIBLE
        } else {
            mainBinding.progressBar.visibility = View.GONE
        }
    }
}