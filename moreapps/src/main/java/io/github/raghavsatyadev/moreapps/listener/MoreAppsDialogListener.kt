package io.github.raghavsatyadev.moreapps.listener

import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails

interface MoreAppsDialogListener {
    fun onClose()
    fun onAppClicked(appsModel: MoreAppsDetails)
}