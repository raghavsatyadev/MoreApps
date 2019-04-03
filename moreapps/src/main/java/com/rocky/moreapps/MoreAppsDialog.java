package com.rocky.moreapps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rocky.moreapps.adapter.MoreAppsBaseAdapter;
import com.rocky.moreapps.adapter.MoreAppsListAdapter;
import com.rocky.moreapps.listener.MoreAppsDialogListener;
import com.rocky.moreapps.listener.MoreAppsDownloadListener;
import com.rocky.moreapps.model.MoreAppsDetails;
import com.rocky.moreapps.settings.MoreAppsDesignSettings;
import com.rocky.moreapps.settings.PeriodicUpdateSettings;
import com.rocky.moreapps.utils.MoreAppsPrefUtil;
import com.rocky.moreapps.utils.MoreAppsUtils;

import java.util.ArrayList;
import java.util.List;

public class MoreAppsDialog {
    private final MoreAppsDesignSettings designSettings;
    private final PeriodicUpdateSettings updateSettings;
    private String url;
    private MoreAppsListAdapter adapter;
    private Dialog dialog;

    MoreAppsDialog(String url, MoreAppsDesignSettings designSettings, PeriodicUpdateSettings updateSettings) {
        this.url = url;
        this.designSettings = designSettings;
        this.updateSettings = updateSettings;
    }

    /**
     * to show the More Apps dialog
     *
     * @param context  {@link Context} of Activity or Fragment
     * @param listener {@link MoreAppsDownloadListener} to listen for dialog events
     */
    public void show(final Context context,
                     final MoreAppsDialogListener listener) {
        if (designSettings.getThemeColor() == 0) {
            designSettings.setThemeColor(context, 0);
        }
        final ArrayList<MoreAppsDetails> moreApps = MoreAppsPrefUtil.getMoreApps(context);
        if (!moreApps.isEmpty()) {
            createDialog(context, moreApps, listener);
        } else {
            startWorker(context, new MoreAppsDownloadListener() {
                @Override
                public void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsDetails> moreAppsDetails) {
                    createDialog(context, new ArrayList<>(moreAppsDetails), listener);
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    void startWorker(Context context, MoreAppsDownloadListener listener) {
        MoreAppsWorker.startWorker(context, url, listener, this, designSettings.getThemeColor(), updateSettings);
    }

    private void prepareView(@NonNull Context context, Dialog view, MoreAppsDialogListener listener) {
        TextView txtMoreAppsTitle = view.findViewById(R.id.txt_more_apps_title);
        RecyclerView listMoreApps = view.findViewById(R.id.list_more_apps);
        View closeButton = view.findViewById(R.id.btn_more_apps_close);
        View viewTitleSeparator = view.findViewById(R.id.view_title_separator);

        Typeface fontFace = null;

        if (designSettings.getFont() != 0) {
            fontFace = ResourcesCompat.getFont(context, designSettings.getFont());
        }

        setCloseButton(closeButton, listener);

        setSeparator(viewTitleSeparator);

        setDialogTitle(txtMoreAppsTitle, fontFace);

        setList(context, listMoreApps, fontFace, listener);
    }

    private void createDialog(Context context,
                              final ArrayList<MoreAppsDetails> moreAppsDetails,
                              final MoreAppsDialogListener listener) {
        dialog = new Dialog(context, R.style.Theme_Transparent);
        dialog.setContentView(designSettings.getDialogLayout());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) listener.onClose();
            }
        });
        prepareView(context, dialog, listener);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                adapter.deleteAll();
                for (int i = moreAppsDetails.size() - 1; i >= 0; i--) {
                    if (!moreAppsDetails.get(i).showInDialog ||
                            (!designSettings.getIgnoredPackageNames().isEmpty() && designSettings.getIgnoredPackageNames().contains(moreAppsDetails.get(i).packageName)))
                        moreAppsDetails.remove(i);
                }
                adapter.addAll(moreAppsDetails);
                ViewGroup container = dialog.findViewById(android.R.id.content);
                if (container != null) TransitionManager.beginDelayedTransition(container);
            }
        });
        dialog.show();
    }

    private void setList(final Context context, RecyclerView listMoreApps, Typeface fontFace, final MoreAppsDialogListener listener) {
        if (listMoreApps != null) {
            listMoreApps.setLayoutManager(new LinearLayoutManager(context));

            listMoreApps.setNestedScrollingEnabled(true);
            adapter = new MoreAppsListAdapter(designSettings.getDialogRowLayout(),
                    designSettings.getThemeColor(),
                    fontFace,
                    designSettings.getRowTitleColor(),
                    designSettings.getRowDescriptionColor());

            adapter.setOnItemClickListener(new MoreAppsBaseAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    MoreAppsDetails appsModel = adapter.getItem(position);
                    if (designSettings.shouldOpenInPlayStore()) {
                        MoreAppsUtils.openBrowser(context, appsModel.appLink);
                    }
                    if (listener != null) listener.onAppClicked(appsModel);
                }
            });
            listMoreApps.setAdapter(adapter);
        }
    }

    private void setDialogTitle(TextView txtMoreAppsTitle, Typeface fontFace) {
        if (txtMoreAppsTitle != null) {
            txtMoreAppsTitle.setTextColor(designSettings.getThemeColor());

            if (fontFace != null) txtMoreAppsTitle.setTypeface(fontFace);

            if (!TextUtils.isEmpty(designSettings.getDialogTitle())) {
                txtMoreAppsTitle.setText(designSettings.getDialogTitle());
            }
        }
    }

    private void setSeparator(View viewTitleSeparator) {
        if (viewTitleSeparator != null)
            viewTitleSeparator.setBackgroundColor(designSettings.getThemeColor());
    }

    private void setCloseButton(View closeButton, final MoreAppsDialogListener listener) {
        if (closeButton != null) {
            ViewCompat.setBackgroundTintList(closeButton, ColorStateList.valueOf(designSettings.getThemeColor()));
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (listener != null) listener.onClose();
                }
            });
        }
    }
}
