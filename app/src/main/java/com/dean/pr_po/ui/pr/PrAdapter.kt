package com.dean.pr_po.ui.pr

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dean.pr_po.DetailActivity
import com.dean.pr_po.R
import com.dean.pr_po.databinding.ItemUserBinding
import kotlinx.android.synthetic.main.item_user.view.*

class PrAdapter (private val listUser: ArrayList<UserData>):  RecyclerView.Adapter<PrAdapter.PrViewHolder>(){
    lateinit var mcontext: Context

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrViewHolder(binding)
    }

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: PrViewHolder, position: Int) {
    //    val data = listUser[position]
        holder.bind(listUser[position])
       /* holder.itemView.setOnClickListener {
            val dataUser = UserData(
                data.name
            )
            val intentDetail = Intent(mcontext, DetailActivity::class.java)
          //  intentDetail.putExtra(DetailActivity.EXTRA_DATA, dataUser)
            mcontext.startActivity(intentDetail)
        }*/
    }

    inner class PrViewHolder (private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: UserData) {
            with(binding) {
                txtName.text = user.name

                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(user) }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserData)
    }
}
