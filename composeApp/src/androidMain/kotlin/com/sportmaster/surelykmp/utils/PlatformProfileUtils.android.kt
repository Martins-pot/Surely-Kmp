package com.sportmaster.surelykmp.utils


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


actual fun getPlatformUtils(): PlatformUtils = AndroidPlatformUtils.instance

object AndroidPlatformUtils : PlatformUtils {
    private lateinit var applicationContext: Context

    val instance: PlatformUtils
        get() = this

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    override fun openEmail(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            val chooser = Intent.createChooser(intent, "Send email using...")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "No email client found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun shareText(text: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, title)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(chooser)
    }

    override fun openAppInStore(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }
    }

    override fun copyToClipboard(text: String, label: String) {
        val clipboard = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(applicationContext, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}