package com.binay.shaw.justap.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.databinding.HistoryItemLayoutBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.LocalHistory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


private var historyList: List<LocalHistory> = ArrayList()

class HistoryAdapter(
    val context: Context,
    private val listener: (LocalHistory) -> Unit,
    private val onMenuClick: (LocalHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: HistoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            HistoryItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentAccount = historyList[position]

        val name = currentAccount.username.trim()
        holder.binding.historyUsername.text = name

        currentAccount.addedOn?.let {
            if (it.isNotEmpty()) {
                holder.binding.historyAddedDate.apply {
                    text = it.trim()
                    visibility = View.VISIBLE
                }
            }
        }
        holder.binding.moreMenu.setOnClickListener {
            onMenuClick(currentAccount)
        }
        currentAccount.profileImage?.let {
            Glide.with(context)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.binding.historyProfilePicture)
            val sizeInBytes = it.byteCount
            val sizeInMB = sizeInBytes.toDouble() / (1024 * 1024)
            Util.log("Size of image: $sizeInMB")
        }

        holder.itemView.setOnClickListener {
            listener(currentAccount)
        }

    }

    override fun getItemCount() = historyList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(history: List<LocalHistory>) {
        historyList = history
        notifyDataSetChanged()
    }
}