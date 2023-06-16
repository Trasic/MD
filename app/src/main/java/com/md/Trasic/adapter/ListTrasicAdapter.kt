package com.md.Trasic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.md.Trasic.data.TrasicResponse
import com.md.Trasic.databinding.TrasicItemBinding
import com.md.Trasic.helper.Utils

class ListTrasicAdapter(private val listItem: List<TrasicResponse>) :
    RecyclerView.Adapter<ListTrasicAdapter.ListTrasicViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var binding: TrasicItemBinding

    class ListTrasicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTrasicViewHolder {
        binding = TrasicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListTrasicViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ListTrasicViewHolder, position: Int) {
        listItem[position].let { item ->
            holder.apply {
                binding.itemTitle.text = item.name
                binding.itemDesc.text = item.description

                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .placeholder(Utils.getCircularProgressDrawable(itemView.context))
                    .into(binding.itemImage)

                itemView.setOnClickListener { onItemClickCallback.onItemClicked(item, position) }
            }
        }
    }

    override fun getItemCount(): Int = listItem.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: TrasicResponse, position: Int)
    }
}