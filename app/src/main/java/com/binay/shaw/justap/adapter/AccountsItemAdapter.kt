package com.binay.shaw.justap.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.databinding.SocialAccountLayoutBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.presentation.mainScreens.homeScreen.HomeFragmentDirections


private var accountsList: List<Accounts> = ArrayList()

class AccountsItemAdapter(
    val activity: Activity,
    private val listener: (Accounts) -> Unit
) : RecyclerView.Adapter<AccountsItemAdapter.AccountsViewHolder>() {

    class AccountsViewHolder(val binding: SocialAccountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        return AccountsViewHolder(
            SocialAccountLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = accountsList[position]

        holder.binding.apply {
            accountTitle.text = account.accountName
            accountValue.text = account.accountData

            accountSwitch.apply {
                isChecked = account.showAccount
                setOnTouchListener { _, event ->
                    event.actionMasked == MotionEvent.ACTION_MOVE
                }
                setOnClickListener {
                    if (!Util.checkForInternet(context)) {
                        Util.showNoInternet(activity)
                        holder.binding.accountSwitch.isChecked = account.showAccount
                        return@setOnClickListener
                    }
                    listener(account)
                }
            }

            accountLogo.setImageResource(Util.getImageDrawableFromAccountName(account.accountName))

            root.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeToAddEditFragment(
                    1, Accounts(
                        -1,
                        accountTitle.text.toString(),
                        accountValue.text.toString(),
                        accountSwitch.isEnabled
                    )
                )
                it.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount() = accountsList.size


    @SuppressLint("NotifyDataSetChanged")
    fun setData(accounts: List<Accounts>) {
        accountsList = accounts
        notifyDataSetChanged()
    }

}