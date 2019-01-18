package com.rocky.moreapps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MoreAppsUtils {
    static void openBrowser(Context context, String link) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}
