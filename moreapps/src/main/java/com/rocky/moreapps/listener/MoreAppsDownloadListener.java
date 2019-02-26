package com.rocky.moreapps.listener;

import android.support.annotation.NonNull;

import com.rocky.moreapps.MoreAppsDialog;
import com.rocky.moreapps.model.MoreAppsDetails;

import java.util.List;

public interface MoreAppsDownloadListener {
    void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsDetails> moreAppsDetails);

    void onFailure();
}
