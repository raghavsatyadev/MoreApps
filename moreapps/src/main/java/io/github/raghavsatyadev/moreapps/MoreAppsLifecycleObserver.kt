package io.github.raghavsatyadev.moreapps

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.showUpdateDialogs

class MoreAppsLifecycleObserver(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val updateDialogType: UpdateDialogType,
    private val styleRes: Int,
    private val listener: MoreAppsLifecycleListener?,
) : DefaultLifecycleObserver {
    private var isFirstStart = true
    private var isShowing = false

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun setShowing(showing: Boolean) {
        isShowing = showing
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (!isFirstStart) {
            listener?.onStart()
            if (updateDialogType === HARD_UPDATE || updateDialogType === HARD_REDIRECT) {
                if (!isShowing) {
                    isShowing = true
                    showUpdateDialogs(context, styleRes) { isShowing = false }
                    listener?.showingDialog()
                }
            } else {
                removeObserver()
                listener?.onComplete()
            }
        } else {
            isFirstStart = false
        }
    }

    fun removeObserver() {
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        listener?.onStop()
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
        super.onDestroy(owner)
    }
}