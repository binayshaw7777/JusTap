package com.binay.shaw.justap.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import com.binay.shaw.justap.R
import com.binay.shaw.justap.model.Accounts


object LinksUtils {

    fun processData(accounts: Accounts, context: Context) {
        val accountName = accounts.accountName
        val data = accounts.accountData
        val accountNameArray = context.resources.getStringArray(R.array.account_names)
        when (accountName) {

            accountNameArray[0] -> {
                addToContacts(data, accountName, context)
            }
            accountNameArray[1] -> {
                sendEmail(data, context)
            }
            accountNameArray[2] -> {
                val regex = "instagram.com/"
                if (data.contains(regex) || isLink(data)) {
                    openLink(data, context)
                } else {
                    val instagramLink = "https://www.instagram.com/$data/"
                    openLink(instagramLink, context)
                }
            }
            accountNameArray[3] -> {
                val regex = "linkedin.com/"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val linkedInLink = "https://www.linkedin.com/in/$data"
                    openLink(linkedInLink, context)
                }
            }
            accountNameArray[4] -> {
                val regex = "github.com"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val githubLink = "https://github.com/$data"
                    openLink(githubLink, context)
                }
            }
            accountNameArray[5] -> {
                val regex = "facebook.com"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val faceBookLink = "https://www.facebook.com/$data"
                    openLink(faceBookLink, context)
                }
            }
            accountNameArray[6] -> {
                val regex = "twitter.com/"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val twitterLink = "https://twitter.com/$data"
                    openLink(twitterLink, context)
                }
            }
            accountNameArray[7] -> {
                val regex = "youtube.com/"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val youtubeLink = "https://www.youtube.com/$data"
                    openLink(youtubeLink, context)
                }
            }
            accountNameArray[8] -> {
                val regex = "snapchat.com/add/"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val twitterLink = "https://www.snapchat.com/add/$data"
                    openLink(twitterLink, context)
                }
            }
            accountNameArray[9] -> {
                val regex = "https://www.twitch.tv/"
                if (isLink(data) || data.contains(regex)) {
                    openLink(data, context)
                } else {
                    val twitchLink = "https://www.twitch.tv/$data"
                    openLink(twitchLink, context)
                }
            }
            accountNameArray[10] -> {
                openLink(data, context)
            }
            accountNameArray[11] -> {
                openLink(data, context)
            }
            accountNameArray[12] -> {
                openLink(data, context)
            }
            accountNameArray[13] -> {
                openLink(data, context)
            }
            accountNameArray[14] -> {

                val isPhoneNumber = data.matches("^\\d+\$".toRegex())
                if (isPhoneNumber) {
                    addTelegramAccountByPhone(data, context)
                } else {
                    addTelegramAccountByUsername(data, context)
                }
            }
            accountNameArray[15] -> {
                openLink(data, context)
            }
            accountNameArray[16] -> {
                addContactToWhatsApp(data, context)
            }
        }
    }

    private fun isLink(data: String): Boolean {
        if (data.contains("https://") || data.contains("http://"))
            return true
        return false
    }

    private fun addTelegramAccountByUsername(username: String, context: Context) {
        val telegramPackageName = "org.telegram.messenger"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("tg://resolve?domain=$username")
        intent.setPackage(telegramPackageName)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Open Telegram in browser if the app is not installed
            intent.data = Uri.parse("https://t.me/$username")
            context.startActivity(intent)
        }
    }

    private fun addTelegramAccountByPhone(phoneNumber: String, context: Context) {
        val telegramPackageName = "org.telegram.messenger"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("tg://add?phone=$phoneNumber")
        intent.setPackage(telegramPackageName)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Open Telegram in browser if the app is not installed
            intent.data = Uri.parse("https://t.me/add_contact/?number=$phoneNumber")
            context.startActivity(intent)
        }
    }

    private fun openLink(link: String, context: Context) {
        Util.log("Link Opened is: $link")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    private fun addContactToWhatsApp(number: String, context: Context) {
        val whatsAppURL =
            "https://api.whatsapp.com/send/?phone=$number&text=Hi%20there!&type=phone_number"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsAppURL))
        context.startActivity(intent)
    }

    private fun addToContacts(phone: String, username: String, context: Context) {
        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent.putExtra(ContactsContract.Intents.Insert.NAME, username)
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone)
        context.startActivity(intent)
    }

    private fun sendEmail(email: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.setPackage("com.google.android.gm")
        context.startActivity(Intent.createChooser(intent, "Send Email"))
    }
}