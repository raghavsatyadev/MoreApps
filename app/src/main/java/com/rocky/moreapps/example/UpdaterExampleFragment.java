package com.rocky.moreapps.example;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rocky.moreapps.ForceUpdater;
import com.rocky.moreapps.listener.UpdateDialogListener;
import com.rocky.moreapps.model.MoreAppsDetails;
import com.rocky.moreapps.utils.MoreAppsUtils;

public class UpdaterExampleFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = UpdaterExampleFragment.class.getSimpleName();

    public static UpdaterExampleFragment getInstance() {
        UpdaterExampleFragment fragment = new UpdaterExampleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updater_example, container, false);
        view.findViewById(R.id.btn_1).setOnClickListener(this);
        view.findViewById(R.id.btn_3).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                option1();
                break;
            case R.id.btn_3:
                option3();
                break;
        }
    }

    /**
     * call {@link com.rocky.moreapps.MoreAppsBuilder#build()} first
     */
    public void option1() {
        try {
            if (ForceUpdater.shouldShowUpdateDialogs(getContext())) {
                ForceUpdater.showUpdateDialogs(getContext(), new UpdateDialogListener() {
                    @Override
                    public void onClose() {

                    }
                });
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "option1: ", e);
        }

//        call moreAppsDialog.removeUpdateListener(); in onStop() or onDestroy() or onDestroyView()
    }

    /**
     * call {@link com.rocky.moreapps.MoreAppsBuilder#build()} first
     */
    public void option3() {
        try {
            MoreAppsDetails currentAppModel = MoreAppsUtils.getCurrentAppModel(getContext());
            switch (ForceUpdater.dialogToShow(getContext(), currentAppModel)) {
                case HARD_REDIRECT:
                    ForceUpdater.showHardRedirectDialog(getContext(), currentAppModel);
                    break;
                case SOFT_REDIRECT:
                    ForceUpdater.showSoftRedirectDialog(getContext(), currentAppModel, new UpdateDialogListener() {
                        @Override
                        public void onClose() {

                        }
                    });
                    break;
                case HARD_UPDATE:
                    ForceUpdater.showHardUpdateDialog(getContext(), currentAppModel);
                    break;
                case SOFT_UPDATE:
                    ForceUpdater.showSoftUpdateDialog(getContext(), currentAppModel, new UpdateDialogListener() {
                        @Override
                        public void onClose() {

                        }
                    });
                    break;
                case NONE:
//                    do nothing
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "option3: ", e);
        }
    }
}
