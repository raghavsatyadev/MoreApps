package io.github.raghavsatyadev.moreapps.listener

import io.github.raghavsatyadev.moreapps.MoreAppsDialog
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails

interface MoreAppsDownloadListener {
    fun onSuccess(moreAppsDialog: MoreAppsDialog, moreAppsDetails: List<MoreAppsDetails>)
    fun onFailure()
}