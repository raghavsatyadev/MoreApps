package io.github.raghavsatyadev.moreapps

interface MoreAppsLifecycleListener {
    /**
     * AppCompatActivity or Fragment onStart LifeCycle Method
     */
    fun onStart()

    /**
     * AppCompatActivity or Fragment onStop LifeCycle Method
     */
    fun onStop()

    /**
     * updater dialog is showing, stop other works
     */
    fun showingDialog()

    /**
     * on completing all processes regarding updater, continue other work
     */
    fun onComplete()
}