package com.rocky.moreapps.example

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rocky.moreapps.example.R.drawable
import com.rocky.moreapps.example.R.font
import com.rocky.moreapps.example.R.layout
import com.rocky.moreapps.example.R.mipmap
import com.rocky.moreapps.example.R.string
import io.github.raghavsatyadev.moreapps.MoreAppsBuilder
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDialogListener
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails
import java.util.concurrent.TimeUnit

class MoreAppsExampleFragment : Fragment(), OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(layout.fragment_more_apps_example, container, false)
        view.findViewById<View>(R.id.btn_1).setOnClickListener(this)
        view.findViewById<View>(R.id.btn_2).setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.btn_1) {
            option1()
        } else if (id == R.id.btn_2) {
            option2()
        }
    }

    /**
     * This method shows almost all the options available
     */
    fun option1() {
        MoreAppsBuilder(requireContext(), CoreApp.JSON_FILE_URL)
            .removeApplicationFromList("com.appdroidtechnologies.whatscut") // to remove an application from the list, give package name here
            .removeApplicationFromList(listOf("com.appdroidtechnologies.whatscut")) // to remove applications from the list, give package names here
            .dialogTitle(string.more_apps) // custom dialog title
            .dialogLayout(io.github.raghavsatyadev.moreapps.R.layout.more_apps_view) // custom dialog layout, read more instructions in it's javadoc
            .dialogRowLayout(io.github.raghavsatyadev.moreapps.R.layout.row_more_apps) // custom list item layout, read more instructions in it's javadoc
            .openAppsInPlayStore(true) // on clicking the item, should it open in the play store
            .font(font.sans_bold) // custom font
            .theme(
                Color.parseColor("#F44336"),
                Color.parseColor("#FFFFFF")
            ) // custom theme color, read more in javadoc,
            // default colorPrimary-colorOnPrimary of theme
            .rowTitleColor(Color.parseColor("#000000")) // custom list item title color
            .rowDescriptionColor(Color.parseColor("#888888")) // custom list item description color
            .setPeriodicSettings(
                15,
                TimeUnit.MINUTES,  // set interval of detail updating and showing notifications as required, default is 7 days
                mipmap.ic_launcher,
                drawable.ic_small_icon
            ) // launcher icon and small icon (small icon is optional, small icon should be of single color)
            .buildAndShow(object : MoreAppsDialogListener {
                override fun onClose() {
                    // on dialog close
                }

                override fun onAppClicked(appsModel: MoreAppsDetails) {
                    // on item click
                }
            })
    }

    /**
     * call [MoreAppsBuilder.build] first
     */
    fun option2() {
        CoreApp.instance.getMoreAppsDialog()?.show(requireContext(),
            object : MoreAppsDialogListener {
                override fun onClose() {}
                override fun onAppClicked(appsModel: MoreAppsDetails) {}
            })
    }

    companion object {
        @JvmStatic
        val instance: MoreAppsExampleFragment
            get() {
                val fragment = MoreAppsExampleFragment()
                val args = Bundle()
                fragment.arguments = args
                return fragment
            }
    }
}