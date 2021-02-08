package com.dean.pr_po.ui.pr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dean.pr_po.DetailActivity
import com.dean.pr_po.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_purchaserequisition.*

class PurchaseRequisitionFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val list = ArrayList<UserData>()
    private lateinit var adapter: PrAdapter

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchaserequisition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvUser.setHasFixedSize(true)
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
        rvUser.layoutManager = LinearLayoutManager(activity)
        val listUserAdapter = PrAdapter(list)
        rvUser.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : PrAdapter.OnItemClickCallback{
            override fun onItemClicked(data: UserData) {
                showSelectedHero(data)
            }
        })
    }

    private fun showSelectedHero(user: UserData) {
        val intentDetail = Intent(requireActivity(), DetailActivity::class.java)
        intentDetail.putExtra(DetailActivity.EXTRA_DATA, user)
        startActivity(intentDetail)
    }
}