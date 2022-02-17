package io.github.raghavsatyadev.moreapps.listener;

import androidx.annotation.NonNull;

import io.github.raghavsatyadev.moreapps.MoreAppsDialog;
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails;

import java.util.List;

public interface MoreAppsDownloadListener {
    void onSuccess(MoreAppsDialog moreAppsDialog, @NonNull List<MoreAppsDetails> moreAppsDetails);

    void onFailure();
}
