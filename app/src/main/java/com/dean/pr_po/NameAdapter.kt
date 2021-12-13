package com.dean.pr_po

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dean.pr_po.databinding.ItemNameBinding

class NameAdapter (private val nameUser: ArrayList<UserData>):  RecyclerView.Adapter<NameAdapter.NameViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        val binding = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NameViewHolder(binding)
    }

    override fun getItemCount(): Int = nameUser.size

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        holder.bind(nameUser[position])
        holder.binding.cvItem.setOnClickListener {
            onItemClickCallback.onItemClicked(nameUser[holder.adapterPosition])
        }

    }

    inner class NameViewHolder(var binding: ItemNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserData) {
            with(binding) {
                tvItemName.text = user.name
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserData)
    }
}