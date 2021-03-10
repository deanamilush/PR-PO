package com.dean.pr_po

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var mainBinding: ActivityMainBinding
    private val list = ArrayList<UserData>()
    private val listUserAdapter = ListAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        listUserAdapter.notifyDataSetChanged()
        mainBinding.recyclerView.adapter = listUserAdapter


        val pData = intent.getParcelableExtra<UserData>(pDATA) as UserData

        getListUser()
        mainBinding.recyclerView.setHasFixedSize(true)
        //showList()
    }

    fun getListUser() {
        val pData = intent.getParcelableExtra<UserData>(pDATA) as UserData
        val client = AsyncHttpClient()
        val url = "http://192.168.1.8/GlobalInc/valPrPO.php"
        val params = RequestParams()
        params.put("ashost", pData.pAshost)
        params.put("sysnr", pData.pSysnr)
        params.put("client", pData.pClient)
        params.put("usap", pData.pUser_sap)
        params.put("psap", pData.pPass_sap)
        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("return")
                    val tPurc = responseObject.getJSONArray("t_purc")
                    val tPr = responseObject.getJSONArray("t_pr")
                    val tPo = responseObject.getJSONArray("t_po")
                    val type = jsonArray.getJSONObject(0)
                    val typeReturn = type.getString("type")
                    val messageReturn = type.getString("msg")
                    if (typeReturn.equals("E")) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("Error")
                        builder.setMessage(messageReturn)
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.cancel()
                        }
                        builder.show()
                    } else{
                        for (i in 0 until jsonArray.length()){
                            val user = tPurc.getJSONObject(i)
                            val userData = UserData()
                            userData.name = user.getString("BEDNR")
                            val lBednr = user.getString("BEDNR")

                            for (j in 0 until jsonArray.length()) {
                                val dataPr = tPr.getJSONObject(j)
                                if (lBednr.equals(dataPr.getString("BEDNR"))){
                                    userData.prThisMonth = dataPr.getInt("QCUR_MT")
                                    userData.prLastMonth = dataPr.getInt("QPREV_MT")
                                    userData.prMonthAgo = dataPr.getInt("QLAST_MT")
                                    break
                                }
                            }
                            for (k in 0 until jsonArray.length()) {
                                val dataPr = tPr.getJSONObject(k)
                                if (lBednr.equals(dataPr.getString("BEDNR"))){
                                    userData.poThisMonth = dataPr.getInt("QCUR_MT")
                                    userData.poLastMonth = dataPr.getInt("QPREV_MT")
                                    userData.poMonthAgo = dataPr.getInt("QLAST_MT")
                                    break
                                }
                            }

                            list.add(userData)
                            listUserAdapter.notifyDataSetChanged()
                        }

                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showList(){
        mainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = ListAdapter(list)
        mainBinding.recyclerView.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback{
            override fun onItemClicked(data: UserData) {
                showSelected(data)
            }
        })
    }

    private fun showSelected(user: UserData) {
        val intentDetail = Intent(this, DetailActivity::class.java)
        intentDetail.putExtra(DetailActivity.EXTRA_DATA, user)
        startActivity(intentDetail)
    }
}