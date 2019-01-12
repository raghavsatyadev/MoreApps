package com.rocky.moreapps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.FontRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class MoreAppsDialog {
    private static final String TAG = MoreAppsDialog.class.getSimpleName();
    private final String url;
    private final boolean shouldOpenInPlayStore;
    private final int dialogLayout;
    private final int dialogRowLayout;
    private final String dialogTitle;
    private final String removePackageName;
    private int themeColor;
    private final int font;
    private final int rowTitleColor;
    private final int rowDescriptionColor;
    private OkHttpClient okHttpClient;
    private MoreAppsListAdapter adapter;
    private Dialog dialog;
    private UpdateListener updateListener;
    private UpdateListener.UpdateStatus updateStatus = UpdateListener.UpdateStatus.NOT_STARTED;

    private MoreAppsDialog(@NonNull String url,
                           boolean shouldOpenInPlayStore,
                           @LayoutRes int dialogLayout,
                           @LayoutRes int dialogRowLayout,
                           String dialogTitle,
                           String removePackageName,
                           @ColorInt int themeColor,
                           @FontRes int font,
                           @ColorInt int rowTitleColor,
                           @ColorInt int rowDescriptionColor,
                           OkHttpClient okHttpClient) {
        this.url = url;
        this.shouldOpenInPlayStore = shouldOpenInPlayStore;
        this.dialogLayout = dialogLayout;
        this.dialogRowLayout = dialogRowLayout;
        this.dialogTitle = dialogTitle;
        this.removePackageName = removePackageName;
        this.themeColor = themeColor;
        this.font = font;
        this.rowTitleColor = rowTitleColor;
        this.rowDescriptionColor = rowDescriptionColor;
        this.okHttpClient = okHttpClient;
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder builder) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                builder.sslSocketFactory(new Tls12SocketFactory(), trustManager);

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                builder.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return builder;
    }

    private static String getPrimaryColorInHex(@NonNull Context context) {
        TypedValue outValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);
        } else {
            // get color defined for AppCompat
            int appCompatAttribute = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
            context.getTheme().resolveAttribute(appCompatAttribute, outValue, true);
        }
        return String.format("#%06X", (0xFFFFFF & outValue.data));
    }

    /**
     * call this method only to wait for API call to finish
     *
     * @param updateListener {@link UpdateDialogListener}
     */
    public void addUpdateListener(UpdateListener updateListener) {
        if (updateStatus == UpdateListener.UpdateStatus.COMPLETE) {
            updateListener.onEvent(UpdateListener.UpdateStatus.COMPLETE);
        } else {
            this.updateListener = updateListener;
        }
    }

    public void removeUpdateListener() {
        this.updateListener = null;
    }

    /**
     * check whether to show the dialogs or not
     * <p>
     * This method will check {@link android.content.SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link Builder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @param context {@link Context of Activity or Fragment}
     */
    public boolean shouldShowUpdateDialogs(Context context) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        MoreAppsModel moreAppsModel = getCurrentAppModel(context);
        if (moreAppsModel != null) {
            return moreAppsModel.redirectDetails != null && moreAppsModel.redirectDetails.enable ||
                    moreAppsModel.minVersion > versionCode || moreAppsModel.currentVersion > versionCode;
        } else
            return false;
    }

    /**
     * call this method to show the update dialogs
     * <p>
     * This method will check {@link android.content.SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link Builder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @param context              {@link Context of Activity or Fragment}
     * @param updateDialogListener to listen for dialog close events
     */
    public void showUpdateDialogs(Context context, UpdateDialogListener updateDialogListener) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        MoreAppsModel moreAppsModel = getCurrentAppModel(context);
        if (moreAppsModel != null) {
            if (moreAppsModel.redirectDetails != null && moreAppsModel.redirectDetails.enable) {
                showRedirect(context, moreAppsModel, updateDialogListener);
            } else if (moreAppsModel.minVersion > versionCode) {
                showHardUpdateDialog(context, moreAppsModel);
            } else if (moreAppsModel.currentVersion > versionCode) {
                showSoftUpdateDialog(context, moreAppsModel, updateDialogListener);
            }
        }
    }

    /**
     * to know which type of dialog is need to show
     *
     * @param moreAppsModel {@link MoreAppsModel} of current app, get this by calling {@link MoreAppsDialog#getCurrentAppModel}
     * @return {@link UpdateDialogType}
     */
    public UpdateDialogType dialogToShow(Context context, MoreAppsModel moreAppsModel) throws PackageManager.NameNotFoundException {
        if (moreAppsModel != null) {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

            if (moreAppsModel.redirectDetails != null && moreAppsModel.redirectDetails.enable) {
                return moreAppsModel.redirectDetails.hardRedirect ? UpdateDialogType.HARD_REDIRECT : UpdateDialogType.SOFT_REDIRECT;
            } else if (moreAppsModel.minVersion > versionCode) {
                return UpdateDialogType.HARD_UPDATE;
            } else if (moreAppsModel.currentVersion > versionCode) {
                return UpdateDialogType.SOFT_UPDATE;
            }
        }
        return UpdateDialogType.NONE;
    }

    /**
     * NOTE : call {@link Builder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @return {@link MoreAppsModel} of current app if present
     */
    public MoreAppsModel getCurrentAppModel(Context context) {
        ArrayList<MoreAppsModel> moreApps = SharedPrefsUtil.getMoreApps(context);
        if (moreApps != null && !moreApps.isEmpty()) {
            String currentPackageName = context.getPackageName();
            for (int i = 0; i < moreApps.size(); i++) {
                MoreAppsModel moreAppsModel = moreApps.get(i);
                if (moreAppsModel.packageName.equals(currentPackageName)) {
                    return moreAppsModel;
                }
            }
        }
        return null;
    }

    /**
     * @param context       {@link Context of Activity or Fragment}
     * @param moreAppsModel {@link MoreAppsModel}
     * @param listener      {@link UpdateDialogListener} to know when dialog is closed
     */
    public void showSoftUpdateDialog(final Context context, final MoreAppsModel moreAppsModel, final UpdateDialogListener listener) {
        if (moreAppsModel != null && moreAppsModel.softUpdateDetails != null) {
            SoftUpdateDetails softUpdateDetails = moreAppsModel.softUpdateDetails;
            new AlertDialog.Builder(context)
                    .setTitle(softUpdateDetails.dialogTitle)
                    .setMessage(softUpdateDetails.dialogMessage)
                    .setPositiveButton(softUpdateDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBrowser(context, moreAppsModel.appLink);
                        }
                    })
                    .setNegativeButton(softUpdateDetails.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (listener != null) listener.onClose();
                        }
                    })
                    .setCancelable(true)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (listener != null) listener.onClose();
                        }
                    })
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context of Activity or Fragment}
     * @param moreAppsModel {@link MoreAppsModel}
     */
    public void showHardUpdateDialog(final Context context, final MoreAppsModel moreAppsModel) {
        if (moreAppsModel != null && moreAppsModel.hardUpdateDetails != null) {
            HardUpdateDetails hardUpdateDetails = moreAppsModel.hardUpdateDetails;
            new AlertDialog.Builder(context)
                    .setTitle(hardUpdateDetails.dialogTitle)
                    .setMessage(hardUpdateDetails.dialogMessage)
                    .setPositiveButton(hardUpdateDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBrowser(context, moreAppsModel.appLink);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context of Activity or Fragment}
     * @param moreAppsModel {@link MoreAppsModel}
     */
    public void showHardRedirect(final Context context, final MoreAppsModel moreAppsModel) {
        if (moreAppsModel != null && moreAppsModel.redirectDetails != null) {
            final RedirectDetails redirectDetails = moreAppsModel.redirectDetails;
            new AlertDialog.Builder(context)
                    .setTitle(redirectDetails.dialogTitle)
                    .setMessage(redirectDetails.dialogMessage)
                    .setPositiveButton(redirectDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBrowser(context, redirectDetails.appLink);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context of Activity or Fragment}
     * @param moreAppsModel {@link MoreAppsModel}
     * @param listener      {@link UpdateDialogListener} to know when dialog is closed
     */
    public void showSoftRedirect(final Context context, MoreAppsModel moreAppsModel, final UpdateDialogListener listener) {
        if (moreAppsModel != null && moreAppsModel.redirectDetails != null) {
            final RedirectDetails redirectDetails = moreAppsModel.redirectDetails;
            new AlertDialog.Builder(context)
                    .setTitle(redirectDetails.dialogTitle)
                    .setMessage(redirectDetails.dialogMessage)
                    .setPositiveButton(redirectDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBrowser(context, redirectDetails.appLink);
                        }
                    })
                    .setNegativeButton(redirectDetails.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) listener.onClose();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (listener != null) listener.onClose();
                        }
                    })
                    .setCancelable(true)
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context of Activity or Fragment}
     * @param moreAppsModel {@link MoreAppsModel}
     * @param listener      {@link UpdateDialogListener} to know when dialog is closed
     */
    public void showRedirect(final Context context, final MoreAppsModel moreAppsModel, final UpdateDialogListener listener) {
        if (moreAppsModel != null && moreAppsModel.redirectDetails != null) {
            if (!moreAppsModel.redirectDetails.hardRedirect) {
                showSoftRedirect(context, moreAppsModel, listener);
            } else {
                showHardRedirect(context, moreAppsModel);
            }
        }
    }

    /**
     * to show the More Apps dialog
     *
     * @param context  {@link Context of Activity or Fragment}
     * @param listener {@link MoreAppsDownloadListener} to listen for dialog events
     */
    public void show(final Context context, final MoreAppsDialogListener listener) {
        ArrayList<MoreAppsModel> moreApps = SharedPrefsUtil.getMoreApps(context);
        if (!moreApps.isEmpty()) {
            create(context, listener, moreApps);
        } else {
            updateApps(context, new MoreAppsDownloadListener() {
                @Override
                public void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsModel> moreAppsModels) {
                    create(context, listener, new ArrayList<>(moreAppsModels));
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });
        }
    }

    private void prepareView(@NonNull Context context, Dialog view, MoreAppsDialogListener listener) {
        TextView txtMoreAppsTitle = view.findViewById(R.id.txt_more_apps_title);
        RecyclerView listMoreApps = view.findViewById(R.id.list_more_apps);
        View closeButton = view.findViewById(R.id.btn_more_apps_close);
        View viewTitleSeparator = view.findViewById(R.id.view_title_separator);

        Typeface fontFace = null;

        if (font != 0) {
            fontFace = ResourcesCompat.getFont(context, this.font);
        }

        if (themeColor == 0) {
            themeColor = Color.parseColor(getPrimaryColorInHex(context));
        }

        setCloseButton(closeButton, listener);

        setSeparator(viewTitleSeparator);

        setDialogTitle(txtMoreAppsTitle, fontFace);

        setList(context, listMoreApps, fontFace, listener);
    }

    private void create(Context context, final MoreAppsDialogListener listener, final ArrayList<MoreAppsModel> moreAppsModels) {
        dialog = new Dialog(context, R.style.Theme_Transparent);
        dialog.setContentView(dialogLayout);
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
                if (!TextUtils.isEmpty(removePackageName)) {
                    for (int i = moreAppsModels.size() - 1; i >= 0; i--) {
                        if (moreAppsModels.get(i).packageName.equals(removePackageName))
                            moreAppsModels.remove(i);
                    }
                }
                adapter.addAll(moreAppsModels);
            }
        });
        dialog.show();
    }

    private void setList(final Context context, RecyclerView listMoreApps, Typeface fontFace, final MoreAppsDialogListener listener) {
        if (listMoreApps != null) {
            listMoreApps.setLayoutManager(new LinearLayoutManager(context));

            listMoreApps.setNestedScrollingEnabled(true);
            adapter = new MoreAppsListAdapter(dialogRowLayout, themeColor, fontFace, rowTitleColor, rowDescriptionColor);
            adapter.setOnItemClickListener(new GenRecyclerAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    MoreAppsModel appsModel = adapter.getItem(position);
                    if (shouldOpenInPlayStore) {
                        openBrowser(context, appsModel.appLink);
                    }
                    if (listener != null) listener.onAppClicked(appsModel);
                }
            });
            listMoreApps.setAdapter(adapter);
        }
    }

    private void openBrowser(Context context, String link) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    private void setDialogTitle(TextView txtMoreAppsTitle, Typeface fontFace) {
        if (txtMoreAppsTitle != null) {
            txtMoreAppsTitle.setTextColor(themeColor);

            if (fontFace != null) txtMoreAppsTitle.setTypeface(fontFace);

            if (!TextUtils.isEmpty(dialogTitle)) {
                txtMoreAppsTitle.setText(dialogTitle);
            }
        }
    }

    private void setSeparator(View viewTitleSeparator) {
        if (viewTitleSeparator != null)
            viewTitleSeparator.setBackgroundColor(themeColor);
    }

    private void setCloseButton(View closeButton, final MoreAppsDialogListener listener) {
        if (closeButton != null) {
            ViewCompat.setBackgroundTintList(closeButton, ColorStateList.valueOf(themeColor));
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (listener != null) listener.onClose();
                }
            });
        }
    }

    /**
     * call this method immediately after creating the object of {@link MoreAppsDialog}
     * this method saves the result in SharedPreferences.
     * call this method whenever update is necessary
     *
     * @param context  Context of anything
     * @param listener {@link MoreAppsDownloadListener} to listen for API call
     */
    private void updateApps(@NonNull final Context context, final MoreAppsDownloadListener listener) {
        Uri uri = Uri.parse(url);
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            updateStatus = UpdateListener.UpdateStatus.PROCESSING;
            if (updateListener != null)
                updateListener.onEvent(UpdateListener.UpdateStatus.PROCESSING);
            new Retrofit.Builder()
                    .client(okHttpClient != null ? okHttpClient : enableTls12OnPreLollipop(new OkHttpClient.Builder()).build())
                    .baseUrl(url.substring(0, url.length() - lastPathSegment.length()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MoreAppsApi.class)
                    .getAppModel(lastPathSegment)
                    .enqueue(new Callback<List<MoreAppsModel>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<MoreAppsModel>> call, @NonNull Response<List<MoreAppsModel>> response) {

                            List<MoreAppsModel> body = response.body();
                            if (body != null) {
                                SharedPrefsUtil.setMoreApps(context, body);
                                updateStatus = UpdateListener.UpdateStatus.COMPLETE;
                                if (updateListener != null)
                                    updateListener.onEvent(UpdateListener.UpdateStatus.COMPLETE);
                                if (listener != null)
                                    listener.onSuccess(MoreAppsDialog.this, body);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<MoreAppsModel>> call, @NonNull Throwable t) {
                            updateStatus = UpdateListener.UpdateStatus.FAILURE;
                            if (updateListener != null) {
                                updateListener.onEvent(UpdateListener.UpdateStatus.FAILURE);
                                updateListener.onFailure(t);
                            }
                            if (listener != null) listener.onFailure(t);
                        }
                    });
        }
    }

    public enum UpdateDialogType {
        NONE, SOFT_UPDATE, HARD_UPDATE, SOFT_REDIRECT, HARD_REDIRECT
    }

    public interface MoreAppsApi {
        @GET
        Call<List<MoreAppsModel>> getAppModel(@Url String url);
    }

    public static class Builder {
        private Context context;
        private String url;
        private boolean shouldOpenInPlayStore = true;
        private int dialogLayout = R.layout.more_apps_view;
        private int dialogRowLayout = R.layout.row_more_apps;
        private String dialogTitle = "";
        private String removePackageName = "";
        private int themeColor = 0;
        private int font;
        private int rowTitleColor;
        private int rowDescriptionColor;
        private OkHttpClient okHttpClient;

        /**
         * @param context context
         * @param url     URL of JSON file
         */
        public Builder(@NonNull Context context, @NonNull String url) {
            this.context = context;
            this.url = url;
        }

        /**
         * @param shouldOpenInPlayStore should open apps in Play Store
         * @return {@link Builder}
         */
        public Builder openAppsInPlayStore(boolean shouldOpenInPlayStore) {
            this.shouldOpenInPlayStore = shouldOpenInPlayStore;
            return this;
        }

        /**
         * to remove application from the list
         *
         * @param packageName Package name of application
         * @return {@link Builder}
         */
        public Builder removeApplicationFromList(String packageName) {
            this.removePackageName = packageName;
            return this;
        }

        /**
         * call this method to show custom dialog.
         * <p>
         * NOTE : keep the IDs of the views same as given below
         * <p>
         * Dialog Title (TextView) : txt_more_apps_title
         * <p>
         * RecyclerView : list_more_apps
         * <p>
         * Dialog Close Button (View): btn_more_apps_close
         *
         * @param dialogLayout layout file for dialog
         * @return {@link Builder}
         */
        public Builder dialogLayout(@LayoutRes int dialogLayout) {
            this.dialogLayout = dialogLayout;
            return this;
        }

        /**
         * call this method to show custom dialog.
         * <p>
         * NOTE : keep the IDs of the views same as given below
         * <p>
         * App Image (ImageView) : img_more_apps
         * <p>
         * App Name (TextView): txt_more_apps_name
         * <p>
         * App Rating (SimpleRatingBar): rating_more_apps
         * <p>
         * App Description (TextView): txt_more_apps_description
         *
         * @param dialogRowLayout layout file for Recycler View items
         * @return {@link Builder}
         */
        public Builder dialogRowLayout(@LayoutRes int dialogRowLayout) {
            this.dialogRowLayout = dialogRowLayout;
            return this;
        }

        /**
         * @param dialogTitle custom dialog title
         * @return {@link Builder}
         */
        public Builder dialogTitle(@StringRes int dialogTitle) {
            this.dialogTitle = context.getString(dialogTitle);
            return this;
        }

        /**
         * @param themeColor changes dialog title, rating bar, close button color
         * @return {@link Builder}
         */
        public Builder themeColor(@ColorInt int themeColor) {
            this.themeColor = themeColor;
            return this;
        }

        /**
         * @param dialogTitle custom dialog title
         * @return {@link Builder}
         */
        public Builder dialogTitle(String dialogTitle) {
            this.dialogTitle = dialogTitle;
            return this;
        }

        /**
         * @param font font to apply on whole dialog
         * @return {@link Builder}
         */
        public Builder font(@FontRes int font) {
            this.font = font;
            return this;
        }

        /**
         * @param rowTitleColor {@link ColorInt} color app title in list
         */
        public Builder rowTitleColor(@ColorInt int rowTitleColor) {
            this.rowTitleColor = rowTitleColor;
            return this;
        }

        /**
         * @param rowDescriptionColor {@link ColorInt} color app description in list
         * @return
         */
        public Builder rowDescriptionColor(@ColorInt int rowDescriptionColor) {
            this.rowDescriptionColor = rowDescriptionColor;
            return this;
        }

        /**
         * custom OkHTTPClient for advance usage like network logging, private key access, etc.
         * default client has support of TLS 1.2 for accessing JSON file through Github
         *
         * @param okHttpClient {@link OkHttpClient}
         */
        public Builder customOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public void buildAndShow(MoreAppsDialogListener listener) {
            build(true, null, listener);
        }

        private MoreAppsDialog build(boolean shouldShow, MoreAppsDownloadListener listener, MoreAppsDialogListener dialogListener) {
            MoreAppsDialog moreAppsDialog = new MoreAppsDialog(
                    url,
                    shouldOpenInPlayStore,
                    dialogLayout,
                    dialogRowLayout,
                    dialogTitle,
                    removePackageName,
                    themeColor,
                    font,
                    rowTitleColor,
                    rowDescriptionColor,
                    okHttpClient);

            if (shouldShow) moreAppsDialog.show(context, dialogListener);
            else moreAppsDialog.updateApps(context, listener);
            return moreAppsDialog;
        }

        public MoreAppsDialog build() {
            return build(null);
        }

        public MoreAppsDialog build(MoreAppsDownloadListener listener) {
            return build(false, listener, null);
        }
    }
}
