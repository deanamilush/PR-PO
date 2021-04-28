package com.dean.pr_po

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dean.pr_po.databinding.ItemUserBinding

class ListAdapter (private val listUser: ArrayList<UserData>):  RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUser[position])

    }

    inner class ListViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserData) {
            with(binding) {
                tvItemName.text = user.name
                prThisMonth.text = user.prThisMonth.toString()
                prLastMonth.text = user.prLastMonth.toString()
                prMonthAgo.text = user.prMonthAgo.toString()
                poThisMonth.text = user.poThisMonth.toString()
                poLastMonth.text = user.poLastMonth.toString()
                poMonthAgo.text = user.poMonthAgo.toString()
            }
        }
    }
}