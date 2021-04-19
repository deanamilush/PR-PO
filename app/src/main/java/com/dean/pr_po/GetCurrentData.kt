package com.dean.pr_po

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class GetCurrentData : JobService() {

    companion object {
        private val TAG = GetCurrentData::class.java.simpleName
    }
    val listAllUser = MutableLiveData<ArrayList<UserData>>()
    val list = ArrayList<UserData>()
    val adapter = ListAdapter(list)

    fun getListUsers(): LiveData<ArrayList<UserData>> {
        return listAllUser
    }

    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStartJob()")
        getData(params)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStopJob()")
        return true
    }

    fun getData(job: JobParameters) {
        Log.d(TAG, "getCurrentWeather: Mulai.....")
        val client = AsyncHttpClient()
        val DEFAULT_TIMEOUT = 40 * 1000
        client.setTimeout(DEFAULT_TIMEOUT)
        val params = RequestParams()
        params.put("ashost", "192.168.1.194")
        params.put("sysnr", "00")
        params.put("client", "100")
        params.put("usap", "GSG-TEST")
        params.put("psap", "123456")
        val url = "http://192.168.1.8/GlobalInc/valPrPo.php"
        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

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
                            Toast.makeText(this@GetCurrentData, messageErrorLogin, Toast.LENGTH_SHORT).show()
                            val builder = AlertDialog.Builder(this@GetCurrentData)
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

                                    list.add(userData)
                                    listAllUser.postValue(list)

                                    /*list.add(userData)
                                    adapter.setData(list)
                                    adapter.notifyDataSetChanged()*/

                                }
                            }
                        }
                    }

                    Log.d(TAG, "onSuccess: Selesai.....")
                    jobFinished(job, false)

                } catch (e: Exception) {
                    Log.d(TAG, "onSuccess: Gagal.....")
                    jobFinished(job, true)
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray, error: Throwable) {
                Log.d(TAG, "onFailure: Gagal.....")
                jobFinished(job, true)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"

                }

                val builder = AlertDialog.Builder(this@GetCurrentData)
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

}