package com.rocky.moreapps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
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

import androidx.annotation.ColorInt;
import androidx.annotation.FontRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private AppListAdapter adapter;
    private Dialog dialog;

    private MoreAppsDialog(@NonNull String url,
                           boolean shouldOpenInPlayStore,
                           @LayoutRes int dialogLayout,
                           @LayoutRes int dialogRowLayout,
                           String dialogTitle,
                           String removePackageName,
                           @ColorInt int themeColor,
                           @FontRes int font,
                           @ColorInt int rowTitleColor,
                           @ColorInt int rowDescriptionColor) {
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
    }

    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
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

                client.sslSocketFactory(new Tls12SocketFactory(), trustManager);

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }

    /**
     * to show the More Apps dialog
     *
     * @param context  context
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

    private static String getThemeColorInHex(@NonNull Context context) {
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
            themeColor = Color.parseColor(getThemeColorInHex(context));
        }

        setCloseButton(closeButton, listener);

        setSeparator(viewTitleSeparator);

        setDialogTitle(txtMoreAppsTitle, fontFace);

        setList(context, listMoreApps, fontFace, listener);
    }

    private void setList(final Context context, RecyclerView listMoreApps, Typeface fontFace, final MoreAppsDialogListener listener) {
        if (listMoreApps != null) {
            listMoreApps.setLayoutManager(new LinearLayoutManager(context));

            listMoreApps.setNestedScrollingEnabled(true);
            adapter = new AppListAdapter(dialogRowLayout, themeColor, fontFace, rowTitleColor, rowDescriptionColor);
            adapter.setOnItemClickListener(new GenRecyclerAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    MoreAppsModel appsModel = adapter.getItem(position);
                    if (shouldOpenInPlayStore) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appsModel.playStoreLink)));
                    }
                    if (listener != null) listener.onAppClicked(appsModel);
                }
            });
            listMoreApps.setAdapter(adapter);
        }
    }

    private void setDialogTitle(TextView txtMoreAppsTitle, Typeface fontFace) {
        if (txtMoreAppsTitle != null) {
            if (themeColor != 0) txtMoreAppsTitle.setTextColor(themeColor);

            if (fontFace != null) txtMoreAppsTitle.setTypeface(fontFace);

            if (!TextUtils.isEmpty(dialogTitle)) {
                txtMoreAppsTitle.setText(dialogTitle);
            }
        }
    }

    private void setSeparator(View viewTitleSeparator) {
        if (viewTitleSeparator != null && themeColor != 0)
            viewTitleSeparator.setBackgroundColor(themeColor);
    }

    private void setCloseButton(View closeButton, final MoreAppsDialogListener listener) {
        if (closeButton != null) {
            if (themeColor != 0) {
                ViewCompat.setBackgroundTintList(closeButton, ColorStateList.valueOf(themeColor));
            }
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
            new Retrofit.Builder()
                    .client(enableTls12OnPreLollipop(new OkHttpClient.Builder()).build())
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
                                if (listener != null)
                                    listener.onSuccess(MoreAppsDialog.this, body);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<MoreAppsModel>> call, @NonNull Throwable t) {
                            if (listener != null) listener.onFailure(t);
                        }
                    });
        }
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

        public Builder rowTitleColor(int rowTitleColor) {
            this.rowTitleColor = rowTitleColor;
            return this;
        }

        public Builder rowDescriptionColor(int rowDescriptionColor) {
            this.rowDescriptionColor = rowDescriptionColor;
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
                    rowDescriptionColor);

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
