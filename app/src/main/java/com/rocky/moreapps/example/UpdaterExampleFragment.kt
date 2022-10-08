package com.rocky.moreapps.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rocky.moreapps.example.R.layout
import com.rocky.moreapps.example.R.style
import io.github.raghavsatyadev.moreapps.ForceUpdater
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.NONE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.dialogToShow
import io.github.raghavsatyadev.moreapps.ForceUpdater.shouldShowUpdateDialogs
import io.github.raghavsatyadev.moreapps.ForceUpdater.showHardRedirectDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showHardUpdateDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showSoftRedirectDialog
import io.github.raghavsatyadev.moreapps.ForceUpdater.showSoftUpdateDialog
import io.github.raghavsatyadev.moreapps.MoreAppsLifecycleListener
import io.github.raghavsatyadev.moreapps.listener.MoreAppsUpdateDialogListener
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils.getCurrentAppModel

class UpdaterExampleFragment : Fragment(), OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(layout.fragment_updater_example, container, false)
        view.findViewById<View>(R.id.btn_updater_1).setOnClickListener(this)
        view.findViewById<View>(R.id.btn_updater_2).setOnClickListener(this)
        view.findViewById<View>(R.id.btn_updater_3).setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_updater_1 -> {
                option1()
            }

            R.id.btn_updater_2 -> {
                option2()
            }

            R.id.btn_updater_3 -> {
                option3()
            }
        }
    }

    /** call [io.github.raghavsatyadev.moreapps.MoreAppsBuilder.build] first */
    private fun option1() {
        if (shouldShowUpdateDialogs(requireContext())) {
            ForceUpdater.showUpdateDialogs(requireContext()) {
                // on close
            }
        }

//        call moreAppsDialog.removeUpdateListener(); in onStop() or onDestroy() or onDestroyView()
    }

    private fun option2() {
        ForceUpdater.showDialogLive(requireContext(),
            this,
            style.CustomDialog,
            object : MoreAppsLifecycleListener {
                override fun onStart() {
                    // Activity or Fragment's onStart LifeCycle Method
                }

                override fun onStop() {
                    // Activity or Fragment's onStop LifeCycle Method
                }

                override fun showingDialog() {
                    // stop other work till this dialog is showing
                }

                override fun onComplete() {
                    // do other work
                }
            })
    }

    /** call [io.github.raghavsatyadev.moreapps.MoreAppsBuilder.build] first */
    private fun option3() {
        val currentAppModel = getCurrentAppModel(requireContext())
        when (dialogToShow(requireContext(), currentAppModel)) {
            HARD_REDIRECT -> showHardRedirectDialog(requireContext(),
                currentAppModel,
                MoreAppsUpdateDialogListener {
                    // on close
                })

            SOFT_REDIRECT -> showSoftRedirectDialog(requireContext(),
                currentAppModel,
                MoreAppsUpdateDialogListener {
                    // on close
                })

            HARD_UPDATE -> showHardUpdateDialog(requireContext(),
                currentAppModel,
                MoreAppsUpdateDialogListener {
                    // on close
                })

            SOFT_UPDATE -> showSoftUpdateDialog(requireContext(),
                currentAppModel,
                MoreAppsUpdateDialogListener {
                    // on close
                })

            NONE -> {
                // nothing to update
            }
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