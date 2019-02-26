package com.rocky.moreapps.listener;

import com.rocky.moreapps.model.MoreAppsDetails;

public interface MoreAppsDialogListener {
    void onClose();

    void onAppClicked(MoreAppsDetails appsModel);
}
