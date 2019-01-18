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
import com.rocky.moreapps.MoreAppsDialog;
import com.rocky.moreapps.MoreAppsModel;
import com.rocky.moreapps.UpdateDialogListener;
import com.rocky.moreapps.UpdateListener;

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
     * call {@link MoreAppsDialog.Builder#build()} first
     */
    public void option1() {
        ForceUpdater.addUpdateListener(new UpdateListener() {
            @Override
            public void onEvent(UpdateStatus updateStatus) {
                switch (updateStatus) {
                    case COMPLETE:
                        //                        use this to hide progress bar
                        try {
                            if (ForceUpdater.shouldShowUpdateDialogs(getContext())) {
                                ForceUpdater.showUpdateDialogs(getContext(), new UpdateDialogListener() {
                                    @Override
                                    public void onClose() {

                                    }
                                });
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(TAG, "onEvent: ", e);
                        }
                        break;
                    case PROCESSING:
                        //                        use this to show progress bar
                        break;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //                        use this to hide progress bar
                Log.e(TAG, "onFailure: ", t);
            }
        });

//        call moreAppsDialog.removeUpdateListener(); in onStop() or onDestroy() or onDestroyView()
    }

    @Override
    public void onDestroyView() {
        ForceUpdater.removeUpdateListener();
        super.onDestroyView();
    }

    /**
     * call {@link MoreAppsDialog.Builder#build()} first
     */
    public void option2() {
        try {
            if (ForceUpdater.shouldShowUpdateDialogs(getContext()))
                ForceUpdater.showUpdateDialogs(getContext(), new UpdateDialogListener() {
                    @Override
                    public void onClose() {
                    }
                });
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "option2: ", e);
        }
    }

    /**
     * call {@link MoreAppsDialog.Builder#build()} first
     */
    public void option3() {
        try {
            MoreAppsModel currentAppModel = ForceUpdater.getCurrentAppModel(getContext());
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