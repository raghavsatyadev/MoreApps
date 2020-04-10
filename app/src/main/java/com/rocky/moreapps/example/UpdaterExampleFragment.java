package com.rocky.moreapps.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rocky.moreapps.ForceUpdater;
import com.rocky.moreapps.MoreAppsLifecycleListener;
import com.rocky.moreapps.model.MoreAppsDetails;
import com.rocky.moreapps.utils.MoreAppsUtils;

public class UpdaterExampleFragment extends Fragment implements View.OnClickListener {

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
        view.findViewById(R.id.btn_2).setOnClickListener(this);
        view.findViewById(R.id.btn_3).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                option1();
                break;
            case R.id.btn_2:
                option2();
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
        if (ForceUpdater.shouldShowUpdateDialogs(getContext())) {
            ForceUpdater.showUpdateDialogs(getContext(), () -> {

            });
        }

//        call moreAppsDialog.removeUpdateListener(); in onStop() or onDestroy() or onDestroyView()
    }

    private void option2() {
        ForceUpdater.showDialogLive(getContext(), this, new MoreAppsLifecycleListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onStop() {
            }

            @Override
            public void showingDialog() {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * call {@link com.rocky.moreapps.MoreAppsBuilder#build()} first
     */
    public void option3() {
        MoreAppsDetails currentAppModel = MoreAppsUtils.getCurrentAppModel(getContext());
        switch (ForceUpdater.dialogToShow(getContext(), currentAppModel)) {
            case HARD_REDIRECT:
                ForceUpdater.showHardRedirectDialog(getContext(), currentAppModel);
                break;
            case SOFT_REDIRECT:
                ForceUpdater.showSoftRedirectDialog(getContext(), currentAppModel, () -> {

                });
                break;
            case HARD_UPDATE:
                ForceUpdater.showHardUpdateDialog(getContext(), currentAppModel);
                break;
            case SOFT_UPDATE:
                ForceUpdater.showSoftUpdateDialog(getContext(), currentAppModel, () -> {

                });
                break;
            case NONE:
//                    do nothing
                break;
        }
    }
}
