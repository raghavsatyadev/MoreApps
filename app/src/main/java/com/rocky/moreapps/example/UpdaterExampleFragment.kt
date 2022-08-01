package com.rocky.moreapps.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rocky.moreapps.example.R.layout
import com.rocky.moreapps.example.R.style
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.NONE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.dialogToShow
import io.github.raghavsatyadev.moreapps.ForceUpdater.shouldShowUpdateDialogs
import io.github.raghavsatyadev.moreapps.ForceUpdater.showDialogLive
import io.github.raghavsatyadev.moreapps.ForceUpdater.showHardRedirectDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showHardUpdateDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showSoftRedirectDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showSoftUpdateDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showUpdateDialogs
import io.github.raghavsatyadev.moreapps.MoreAppsLifecycleListener
import io.github.raghavsatyadev.moreapps.listener.MoreAppsUpdateDialogListener
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils.getCurrentAppModel

class UpdaterExampleFragment : Fragment(), OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(layout.fragment_updater_example, container, false)
        view.findViewById<View>(R.id.btn_1).setOnClickListener(this)
        view.findViewById<View>(R.id.btn_2).setOnClickListener(this)
        view.findViewById<View>(R.id.btn_3).setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.btn_1) {
            option1()
        } else if (id == R.id.btn_2) {
            option2()
        } else if (id == R.id.btn_3) {
            option3()
        }
    }

    /**
     * call [MoreAppsBuilder.build] first
     */
    fun option1() {
        if (shouldShowUpdateDialogs(requireContext())) {
            showUpdateDialogs(requireContext(), object : MoreAppsUpdateDialogListener {
                override fun onClose() {
                }
            })
        }

//        call moreAppsDialog.removeUpdateListener(); in onStop() or onDestroy() or onDestroyView()
    }

    private fun option2() {
        showDialogLive(requireContext(),
            this,
            style.CustomDialog,
            object : MoreAppsLifecycleListener {
                override fun onStart() {}
                override fun onStop() {}
                override fun showingDialog() {}
                override fun onComplete() {}
            })
    }

    /**
     * call [MoreAppsBuilder.build] first
     */
    fun option3() {
        val currentAppModel = getCurrentAppModel(requireContext())
        when (dialogToShow(requireContext(), currentAppModel)) {
            HARD_REDIRECT -> showHardRedirectDialog(
                requireContext(), currentAppModel, object : MoreAppsUpdateDialogListener {
                    override fun onClose() {

                    }
                })
            SOFT_REDIRECT -> showSoftRedirectDialog(
                requireContext(),
                currentAppModel,
                object : MoreAppsUpdateDialogListener {
                    override fun onClose() {

                    }
                })
            HARD_UPDATE -> showHardUpdateDialog(
                requireContext(),
                currentAppModel,
                object : MoreAppsUpdateDialogListener {
                    override fun onClose() {

                    }
                })
            SOFT_UPDATE -> showSoftUpdateDialog(
                requireContext(),
                currentAppModel,
                object : MoreAppsUpdateDialogListener {
                    override fun onClose() {

                    }
                })
            NONE -> {}
        }
    }

    companion object {
        val instance: UpdaterExampleFragment
            get() {
                val fragment = UpdaterExampleFragment()
                val args = Bundle()
                fragment.arguments = args
                return fragment
            }
    }
}