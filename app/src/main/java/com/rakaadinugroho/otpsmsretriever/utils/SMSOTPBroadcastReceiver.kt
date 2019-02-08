package com.rakaadinugroho.otpsmsretriever.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSOTPBroadcastReceiver: BroadcastReceiver() {
    var listener: CallbackListener? = null

    fun initCallback(listener: CallbackListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras.get(SmsRetriever.EXTRA_STATUS) as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    val extractPattern = Pattern.compile("\\d{5}")
                    val matcher = extractPattern.matcher(message)
                    if (matcher.find()) {
                        listener?.onOTPReceived(matcher.group(0))
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    listener?.timeOut()
                }

                CommonStatusCodes.ERROR -> {
                    listener?.onError()
                }
            }
        }
    }

    interface CallbackListener {
        fun onOTPReceived(otp: String)
        fun timeOut()
        fun onError()
    }
}