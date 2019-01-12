package com.rocky.moreapps;

public interface UpdateListener {
    void onEvent(UpdateStatus updateStatus);

    void onFailure(Throwable t);

    enum UpdateStatus {
        COMPLETE, PROCESSING, FAILURE
    }
}
