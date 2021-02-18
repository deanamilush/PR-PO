package com.dean.pr_po

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dean.pr_po.databinding.ItemUserBinding

class ListAdapter (private val listUser: ArrayList<UserData>):  RecyclerView.Adapter<ListAdapter.ListViewHolder>(){
    lateinit var mcontext: Context
    private val mData = ArrayList<UserData>()

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(items: ArrayList<UserData>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
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

    inner class ListViewHolder (private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: UserData) {
            with(binding) {
                tvItemName.text = user.name

                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(user) }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserData)
    }
}