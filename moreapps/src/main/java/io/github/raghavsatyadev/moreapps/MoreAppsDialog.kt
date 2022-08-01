package io.github.raghavsatyadev.moreapps

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.raghavsatyadev.moreapps.MoreAppsWorker.Companion.startWorker
import io.github.raghavsatyadev.moreapps.R.id
import io.github.raghavsatyadev.moreapps.R.style
import io.github.raghavsatyadev.moreapps.adapter.MoreAppsBaseAdapter.MyClickListener
import io.github.raghavsatyadev.moreapps.adapter.MoreAppsListAdapter
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDialogListener
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDownloadListener
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails
import io.github.raghavsatyadev.moreapps.settings.MoreAppsDesignSettings
import io.github.raghavsatyadev.moreapps.settings.PeriodicUpdateSettings
import io.github.raghavsatyadev.moreapps.utils.MoreAppsPrefUtil
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils

class MoreAppsDialog internal constructor(
    private val url: String,
    private val designSettings: MoreAppsDesignSettings,
    private val updateSettings: PeriodicUpdateSettings,
) {
    private var adapter: MoreAppsListAdapter? = null
    private var dialog: Dialog? = null

    /**
     * to show the More Apps dialog
     *
     * @param context  [Context] of Activity or Fragment
     * @param listener [MoreAppsDownloadListener] to listen for dialog events
     */
    fun show(
        context: Context,
        listener: MoreAppsDialogListener?,
    ) {
        if (designSettings.primaryColor == 0) {
            designSettings.setTheme(context, 0, 0)
        }
        val moreApps = MoreAppsPrefUtil.getMoreApps(context)
        if (moreApps.isNotEmpty()) {
            createDialog(context, moreApps, listener)
        } else {
            startWorker(context, object : MoreAppsDownloadListener {
                override fun onSuccess(
                    moreAppsDialog: MoreAppsDialog,
                    moreAppsDetails: List<MoreAppsDetails>,
                ) {
                    createDialog(context, ArrayList(moreAppsDetails), listener)
                }

                override fun onFailure() = Unit
            })
        }
    }

    fun startWorker(context: Context?, listener: MoreAppsDownloadListener?) {
        startWorker(context!!, url, listener, this, designSettings.primaryColor, updateSettings)
    }

    private fun prepareView(context: Context, view: Dialog, listener: MoreAppsDialogListener?) {
        val txtMoreAppsTitle = view.findViewById<TextView>(id.txt_more_apps_title)
        val listMoreApps = view.findViewById<RecyclerView>(id.list_more_apps)
        val closeButton = view.findViewById<View>(id.btn_more_apps_close)
        val viewTitleSeparator = view.findViewById<View>(id.view_title_separator)
        var fontFace: Typeface? = null
        if (designSettings.font != 0) {
            fontFace = ResourcesCompat.getFont(context, designSettings.font)
        }
        setCloseButton(closeButton, listener)
        setSeparator(viewTitleSeparator)
        setDialogTitle(txtMoreAppsTitle, fontFace)
        setList(context, listMoreApps, fontFace, listener)
    }

    private fun createDialog(
        context: Context,
        moreAppsDetails: ArrayList<MoreAppsDetails>,
        listener: MoreAppsDialogListener?,
    ) {
        dialog = Dialog(context, style.Theme_Transparent)
        dialog!!.setContentView(designSettings.dialogLayout)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setOnDismissListener { listener?.onClose() }
        prepareView(context, dialog!!, listener)
        Handler(Looper.getMainLooper()).post {
            adapter!!.deleteAll()
            for (i in moreAppsDetails.indices.reversed()) {
                if (!moreAppsDetails[i].showInDialog || designSettings.ignoredPackageNames.isNotEmpty() && designSettings.ignoredPackageNames.contains(
                        moreAppsDetails[i].packageName
                    )
                ) moreAppsDetails.removeAt(i)
            }
            adapter!!.addAll(moreAppsDetails)
            val container = dialog!!.findViewById<ViewGroup>(android.R.id.content)
            if (container != null) TransitionManager.beginDelayedTransition(container)
        }
        dialog!!.show()
    }

    private fun setList(
        context: Context,
        listMoreApps: RecyclerView?,
        fontFace: Typeface?,
        listener: MoreAppsDialogListener?,
    ) {
        if (listMoreApps != null) {
            listMoreApps.layoutManager = LinearLayoutManager(context)
            listMoreApps.isNestedScrollingEnabled = true
            adapter = MoreAppsListAdapter(
                designSettings.dialogRowLayout,
                designSettings.primaryColor,
                fontFace,
                designSettings.rowTitleColor,
                designSettings.rowDescriptionColor
            )
            adapter?.setOnItemClickListener(object : MyClickListener {
                override fun onItemClick(position: Int, v: View?) {
                    val appsModel = adapter!!.getItem(position)
                    if (designSettings.shouldOpenInPlayStore()) {
                        MoreAppsUtils.openBrowser(context, appsModel.appLink)
                    }
                    listener?.onAppClicked(appsModel)
                }
            })
            listMoreApps.adapter = adapter
        }
    }

    private fun setDialogTitle(txtMoreAppsTitle: TextView?, fontFace: Typeface?) {
        if (txtMoreAppsTitle != null) {
            txtMoreAppsTitle.setTextColor(designSettings.primaryColor)
            if (fontFace != null) txtMoreAppsTitle.typeface = fontFace
            if (!TextUtils.isEmpty(designSettings.dialogTitle)) {
                txtMoreAppsTitle.text = designSettings.dialogTitle
            }
        }
    }

    private fun setSeparator(viewTitleSeparator: View?) {
        viewTitleSeparator?.setBackgroundColor(designSettings.primaryColor)
    }

    private fun setCloseButton(closeButton: View?, listener: MoreAppsDialogListener?) {
        if (closeButton != null) {
            ViewCompat.setBackgroundTintList(
                closeButton,
                ColorStateList.valueOf(designSettings.primaryColor)
            )
            if (closeButton is FloatingActionButton) {
                closeButton.supportImageTintList = ColorStateList.valueOf(
                    designSettings.accentColor
                )
            } else if (closeButton is ImageView) {
                ImageViewCompat.setImageTintList(
                    (closeButton as ImageView?)!!, ColorStateList.valueOf(
                        designSettings.accentColor
                    )
                )
            }
            closeButton.setOnClickListener(View.OnClickListener {
                dialog!!.dismiss()
                listener?.onClose()
            })
        }
    }
}