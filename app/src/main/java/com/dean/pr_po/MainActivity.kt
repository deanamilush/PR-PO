package com.dean.pr_po

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dean.pr_po.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.setHasFixedSize(true)
        list.addAll(getListUser())
        showList()
    }

    fun getListUser(): ArrayList<UserData> {
        val dataName = resources.getStringArray(R.array.data_name)
        val listHero = ArrayList<UserData>()
        for (position in dataName.indices) {
            val user = UserData(
                    dataName[position]
            )
            listHero.add(user)
        }
        return listHero
    }

    fun showList(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = ListAdapter(list)
        binding.recyclerView.adapter = listUserAdapter

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