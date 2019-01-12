package com.rocky.moreapps.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rocky.moreapps.BuildConfig;
import com.rocky.moreapps.MoreAppsDialog;
import com.rocky.moreapps.MoreAppsDialogListener;
import com.rocky.moreapps.MoreAppsDownloadListener;
import com.rocky.moreapps.MoreAppsModel;

import java.util.List;

public class MoreAppsDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps_dialog);
    }

    /**
     * This method shows almost all the options available
     */
    public void option1(View view) {
        new MoreAppsDialog.Builder(this, CoreApp.JSON_FILE_URL)
                .removeApplicationFromList("com.appdroidtechnologies.whatscut") // to remove an application from the list, give package name here
                .dialogTitle(R.string.more_apps) // custom dialog title
                .dialogLayout(R.layout.more_apps_view) // custom dialog layout, read more instructions in it's javadoc
                .dialogRowLayout(R.layout.row_more_apps) // custom list item layout, read more instructions in it's javadoc
                .openAppsInPlayStore(true) // on clicking the item, should it open in the play store
                .font(R.font.sans_bold) // custom font
                .themeColor(Color.parseColor("#AAF44336")) // custom theme color, read more in javadoc default primary color
                .rowTitleColor(Color.parseColor("#000000")) // custom list item title color
                .rowDescriptionColor(Color.parseColor("#888888")) // custom list item description color
                .buildAndShow(new MoreAppsDialogListener() {
                    @Override
                    public void onClose() {
                        // on dialog close
                    }

                    @Override
                    public void onAppClicked(MoreAppsModel appsModel) {
                        // on item click
                    }
                });
    }

    /**
     * call {@link MoreAppsDialog.Builder#build()} first
     */
    public void option2(View view) {
        CoreApp.getInstance().getMoreAppsDialog().show(MoreAppsDialogActivity.this, new MoreAppsDialogListener() {
            @Override
            public void onClose() {

            }

            @Override
            public void onAppClicked(MoreAppsModel appsModel) {

            }
        });
    }

    public void option3(View view) {
        new MoreAppsDialog.Builder(this, CoreApp.JSON_FILE_URL)
                .removeApplicationFromList(BuildConfig.APPLICATION_ID)
                .dialogTitle("More Apps")
                .build(new MoreAppsDownloadListener() {
                    @Override
                    public void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsModel> moreAppsModels) {
                        moreAppsDialog.show(MoreAppsDialogActivity.this, new MoreAppsDialogListener() {
                            @Override
                            public void onClose() {

                            }

                            @Override
                            public void onAppClicked(MoreAppsModel appsModel) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {

                    }
                });
    }
}
