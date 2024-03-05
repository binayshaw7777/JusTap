package com.binay.shaw.justap.presentation.account

import com.binay.shaw.justap.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

enum class AccountOptions(val displayName: String, val iconId: Int) {
    EDIT_PROFILE("Edit Profile", R.drawable.ic_account),
    CUSTOMIZE_QR("Customize QR", R.drawable.ic_customize_qr),
    THEME("Theme", R.drawable.ic_theme),
    INVITE_FRIENDS("Invite your friends", R.drawable.ic_invite_friends),
    LANGUAGE("Language", R.drawable.ic_language),
    PRIVACY_POLICY("Privacy Policy", R.drawable.ic_privacy_policy),
    RATE_US("Rate us", R.drawable.ic_google_playstore),
    HELP_AND_SUPPORT("Help and Support", R.drawable.ic_help),
    LOGOUT("Logout", R.drawable.ic_logout);
}