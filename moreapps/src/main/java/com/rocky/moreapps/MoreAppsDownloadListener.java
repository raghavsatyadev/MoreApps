package com.rocky.moreapps;

import java.util.List;

import androidx.annotation.NonNull;

public interface MoreAppsDownloadListener {
    void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsModel> moreAppsModels);

    void onFailure(@NonNull Throwable t);
}
