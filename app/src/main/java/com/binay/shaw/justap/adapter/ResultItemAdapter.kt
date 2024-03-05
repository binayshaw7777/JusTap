package com.binay.shaw.justap.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.databinding.SocialAccountLayoutBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts


class ResultItemAdapter(
    private val listener: (Accounts) -> Unit
) : RecyclerView.Adapter<ResultItemAdapter.ResultsViewHolder>(){

    private var accountsList: List<Accounts> = ArrayList()
    private lateinit var currentAccount: Accounts
    class ResultsViewHolder(val binding: SocialAccountLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        return ResultsViewHolder(
            SocialAccountLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        val account = accountsList[position]
        currentAccount = account
        holder.binding.apply {
            accountTitle.text = account.accountName
            accountValue.text = account.accountData
            accountSwitch.apply {
                isChecked = account.showAccount
                visibility = View.GONE
            }
            accountLogo.setImageResource(Util.getImageDrawableFromAccountName(account.accountName))
            root.setOnClickListener {
                listener(account)
            }
        }
    }

    override fun getItemCount() = accountsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(accounts: List<Accounts>) {
        accountsList = accounts
        notifyDataSetChanged()
    }

    fun clearData() {
        notifyItemRangeRemoved(0, itemCount)
    }
}