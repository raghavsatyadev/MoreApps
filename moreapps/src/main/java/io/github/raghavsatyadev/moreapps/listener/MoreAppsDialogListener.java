package io.github.raghavsatyadev.moreapps.listener;

import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails;

public interface MoreAppsDialogListener {
    void onClose();

    void onAppClicked(MoreAppsDetails appsModel);
}
