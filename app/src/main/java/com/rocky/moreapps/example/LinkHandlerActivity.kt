package com.rocky.moreapps.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import io.github.raghavsatyadev.moreapps.kotlinFileName
import io.github.raghavsatyadev.moreapps.utils.AppLog

class LinkHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_handler)

        handleDynamicLink()
    }

    private fun handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let {
                    findViewById<MaterialTextView>(R.id.txt_first_name).text =
                        it.getQueryParameter("first_name")
                    findViewById<MaterialTextView>(R.id.txt_last_name).text =
                        it.getQueryParameter("last_name")
                    findViewById<MaterialTextView>(R.id.txt_profession).text =
                        it.getQueryParameter("profession")
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