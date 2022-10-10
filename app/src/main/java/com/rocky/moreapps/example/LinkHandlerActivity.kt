package com.rocky.moreapps.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import io.github.raghavsatyadev.moreapps.kotlinFileName
import io.github.raghavsatyadev.moreapps.utils.AppLog
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils

class LinkHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_handler)

        handleDynamicLink()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDynamicLink()
    }

    private fun handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let {
                    findViewById<MaterialTextView>(R.id.txt_manufacturer).text =
                        it.getQueryParameter("manufacturer")
                    findViewById<MaterialTextView>(R.id.txt_model).text =
                        it.getQueryParameter("model")
                    findViewById<MaterialTextView>(R.id.txt_serial).text =
                        it.getQueryParameter("serial")
                    findViewById<MaterialTextView>(R.id.txt_ssid).text =
                        it.getQueryParameter("ssid")
                    findViewById<MaterialTextView>(R.id.txt_password).text =
                        it.getQueryParameter("password")
                    findViewById<MaterialButton>(R.id.btn_open).setOnClickListener { _ ->
                        MoreAppsUtils.openBrowser(this, it.toString())
                    }
                }
            }
            .addOnFailureListener(this) { e ->
                AppLog.loge(
                    false,
                    kotlinFileName,
                    "handleDynamicLink",
                    e,
                    Exception()
                )
            }
    }
}