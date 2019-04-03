package com.rocky.moreapps.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.rocky.moreapps.MoreAppsBuilder;
import com.rocky.moreapps.R;
import com.rocky.moreapps.model.MoreAppsDetails;

import java.util.ArrayList;

public class MoreAppsUtils {
    public static void openBrowser(Context context, String link) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    /**
     * NOTE : call {@link MoreAppsBuilder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @return {@link MoreAppsDetails} of current app if present
     */
    public static MoreAppsDetails getCurrentAppModel(Context context) {
        ArrayList<MoreAppsDetails> moreApps = MoreAppsPrefUtil.getMoreApps(context);
        return getCurrentAppModel(context, moreApps);
    }

    public static MoreAppsDetails getCurrentAppModel(Context context, ArrayList<MoreAppsDetails> moreApps) {
        if (moreApps != null && !moreApps.isEmpty()) {
            String currentPackageName = context.getPackageName();
            for (int i = 0; i < moreApps.size(); i++) {
                MoreAppsDetails moreAppsDetails = moreApps.get(i);
                if (moreAppsDetails.packageName.equals(currentPackageName)) {
                    return moreAppsDetails;
                }
            }
        }
        return null;
    }

    public static String getAttrColorString(Context context, @AttrRes int attrID, String attributeName) {
        TypedValue outValue = new TypedValue();
        int appCompatAttribute = context.getResources().getIdentifier(attributeName, "attr", context.getPackageName());
        context.getTheme().resolveAttribute(appCompatAttribute, outValue, true);
//        }
        return String.format("#%06X", (0xFFFFFF & outValue.data));
    }

    public static String getPrimaryColorInHex(@NonNull Context context) {
        return getAttrColorString(context, R.attr.colorPrimary, "colorPrimary");
    }
}
