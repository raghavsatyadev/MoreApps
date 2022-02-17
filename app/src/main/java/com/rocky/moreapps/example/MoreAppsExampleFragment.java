package com.rocky.moreapps.example;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.github.raghavsatyadev.moreapps.MoreAppsBuilder;
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDialogListener;
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails;

public class MoreAppsExampleFragment extends Fragment implements View.OnClickListener {

    public static MoreAppsExampleFragment getInstance() {
        MoreAppsExampleFragment fragment = new MoreAppsExampleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_apps_example, container, false);
        view.findViewById(R.id.btn_1).setOnClickListener(this);
        view.findViewById(R.id.btn_2).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_1) {
            option1();
        } else if (id == R.id.btn_2) {
            option2();
        }
    }

    /**
     * This method shows almost all the options available
     */
    public void option1() {

        new MoreAppsBuilder(requireContext(), CoreApp.JSON_FILE_URL)
                .removeApplicationFromList("com.appdroidtechnologies.whatscut") // to remove an application from the list, give package name here
                .removeApplicationFromList(Collections.singletonList("com.appdroidtechnologies.whatscut")) // to remove applications from the list, give package names here
                .dialogTitle(R.string.more_apps) // custom dialog title
                .dialogLayout(R.layout.more_apps_view) // custom dialog layout, read more instructions in it's javadoc
                .dialogRowLayout(R.layout.row_more_apps) // custom list item layout, read more instructions in it's javadoc
                .openAppsInPlayStore(true) // on clicking the item, should it open in the play store
                .font(R.font.sans_bold) // custom font
                .theme(Color.parseColor("#F44336"), Color.parseColor("#FFFFFF")) // custom theme color, read more in javadoc,
                // default colorPrimary-colorOnPrimary of theme
                .rowTitleColor(Color.parseColor("#000000")) // custom list item title color
                .rowDescriptionColor(Color.parseColor("#888888")) // custom list item description color
                .setPeriodicSettings(15, TimeUnit.MINUTES, // set interval of detail updating and showing notifications as required, default is 7 days
                        R.mipmap.ic_launcher, R.drawable.ic_small_icon) // launcher icon and small icon (small icon is optional, small icon should be of single color)
                .buildAndShow(new MoreAppsDialogListener() {
                    @Override
                    public void onClose() {
                        // on dialog close
                    }

                    @Override
                    public void onAppClicked(MoreAppsDetails appsModel) {
                        // on item click
                    }
                });
    }

    /**
     * call {@link MoreAppsBuilder#build()} first
     */
    public void option2() {
        CoreApp.getInstance().getMoreAppsDialog().show(requireContext(),
                new MoreAppsDialogListener() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onAppClicked(MoreAppsDetails appsModel) {

                    }
                });
    }
}
