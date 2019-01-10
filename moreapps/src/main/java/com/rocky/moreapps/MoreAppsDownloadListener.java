package com.rocky.moreapps;

import android.support.annotation.NonNull;

import java.util.List;

public interface MoreAppsDownloadListener {
    void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsModel> moreAppsModels);

    void onFailure(@NonNull Throwable t);
}
