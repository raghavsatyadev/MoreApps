package com.rocky.moreapps.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MoreAppsUtils {
    public static void openBrowser(Context context, String link) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}
