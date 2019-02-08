package com.rakaadinugroho.otpsmsretriever

import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.rakaadinugroho.otpsmsretriever.utils.AppSignatureHelper
import com.rakaadinugroho.otpsmsretriever.utils.SMSOTPBroadcastReceiver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val smsotpBroadcastReceiver by lazy { SMSOTPBroadcastReceiver() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("App Signature", "${AppSignatureHelper(context = this).getAppSignatures()}")

        val client = SmsRetriever.getClient(this)
        val retriever = client.startSmsRetriever()

        retriever.addOnSuccessListener {
            val listener = object : SMSOTPBroadcastReceiver.CallbackListener {
                override fun onOTPReceived(otp: String) {
                    Log.e("tag-success",otp)
                    pin_code.setText(otp)
                }
                override fun timeOut() {
                    Log.e("tag-failed","timeout sms")
                }
                override fun onError() {
                    Log.e("tag-failed","error sms")
                }

            }

            smsotpBroadcastReceiver.initCallback(listener)
            registerReceiver(smsotpBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }

        retriever.addOnFailureListener {
            Log.e("tag-failed", it.localizedMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsotpBroadcastReceiver)
    }
}
