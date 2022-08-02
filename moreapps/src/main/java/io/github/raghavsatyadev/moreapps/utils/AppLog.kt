package io.github.raghavsatyadev.moreapps.utils

import android.text.TextUtils
import android.util.Log
import io.github.raghavsatyadev.moreapps.BuildConfig
import io.github.raghavsatyadev.moreapps.utils.AppLog.LogLevel.D
import io.github.raghavsatyadev.moreapps.utils.AppLog.LogLevel.E
import io.github.raghavsatyadev.moreapps.utils.AppLog.LogLevel.I
import io.github.raghavsatyadev.moreapps.utils.AppLog.LogLevel.V
import io.github.raghavsatyadev.moreapps.utils.AppLog.LogLevel.W

@Suppress("MemberVisibilityCanBePrivate", "unused")
object AppLog {
    enum class LogLevel(var displayName: String) {
        D("Debug"), V("Verbose"), E("Error"), I("Info"), W("Warn")
    }

    private val isDebug = BuildConfig.DEBUG

    private val TAG: String = AppLog.javaClass.simpleName

    fun log(
        logLevel: LogLevel,
        isLocal: Boolean,
        tag: String = TAG,
        message: String?,
        throwable: Throwable? = null,
    ) {
        val newTag = if (TextUtils.isEmpty(tag)) TAG else tag
        val newMessage = message ?: ""

        if (!isDebug) {
            logReleaseException(isLocal, throwable)
        } else when (logLevel) {
            D -> Log.d(newTag, newMessage)
            V -> Log.v(newTag, newMessage)
            E -> Log.e(newTag, newMessage, throwable)
            I -> Log.i(newTag, newMessage)
            W -> Log.w(newTag, newMessage, throwable)
        }
    }

    private fun logReleaseException(
        isLocal: Boolean,
        throwable: Throwable? = null,
    ) {
//        if (!isLocal) {
//            throwable?.let { Firebase.crashlytics.recordException(it) }
//        }
    }

    private fun getFormattedLog(
        logLevel: LogLevel,
        tag: String,
        message: String,
    ): String {
        return String.format("%s %s %s", logLevel.displayName, tag, message)
    }

    fun <T> loge(
        isLocal: Boolean,
        fileName: String,
        methodName: String,
        message: T?,
        loggerHelperException: Throwable,
    ) {
        val fileMethodNameFormat =
            getFileMethodNameFormat(fileName, methodName, loggerHelperException)
        log(
            logLevel = E,
            isLocal = isLocal,
            message = fileMethodNameFormat + message?.let { message })
    }

    fun loge(
        isLocal: Boolean,
        fileName: String,
        methodName: String,
        throwable: Throwable?,
        loggerHelperException: Throwable,
    ) {
        val fileMethodNameFormat =
            getFileMethodNameFormat(fileName, methodName, loggerHelperException)

        val exceptionMessage = if (throwable?.message != null) throwable.localizedMessage else ""

        log(
            logLevel = E,
            isLocal = isLocal,
            message = fileMethodNameFormat + exceptionMessage,
            throwable = throwable
        )
    }

    fun loge(
        isLocal: Boolean,
        fileName: String,
        methodName: String,
        exception: Exception?,
        loggerHelperException: Throwable,
    ) {
        val fileMethodNameFormat =
            getFileMethodNameFormat(fileName, methodName, loggerHelperException)

        val exceptionMessage = when {
            exception != null -> {
                when {
                    exception.localizedMessage != null -> exception.localizedMessage
                    exception.cause != null -> exception.cause
                    else -> exception.stackTrace
                }
            }
            else -> ""
        }

        log(
            logLevel = E,
            isLocal = isLocal,
            message = fileMethodNameFormat + exceptionMessage,
            throwable = exception
        )
    }

    fun logMethod(
        isLocal: Boolean,
        fileName: String,
        methodName: String,
        loggerHelperException: Throwable,
    ) {
        log(
            logLevel = E,
            isLocal = isLocal,
            message = getFileLineNumber(fileName, loggerHelperException) + " : " + methodName
        )
    }

    private fun getFileMethodNameFormat(
        fileName: String,
        methodName: String,
        loggerHelperException: Throwable,
    ): String {
        return getFileLineNumber(fileName, loggerHelperException) + " : " + methodName + " : "
    }

    private fun getFileLineNumber(
        fileName: String,
        loggerHelperException: Throwable,
    ): String {
        val stack: StackTraceElement? = getStackElement(loggerHelperException)
        return if (stack != null) "($fileName:${stack.lineNumber})" else ""
    }

    private fun getStackElement(loggerHelperException: Throwable): StackTraceElement? {
        val stackTraceElement: Array<StackTraceElement>? = loggerHelperException.stackTrace
        return stackTraceElement?.firstOrNull()
    }

    private fun getPackageNameFromThrowable(throwable: Throwable): String {
        val stackTraceElement = getStackElement(throwable)
        val className = stackTraceElement?.className
        val pos = className?.lastIndexOf('.')
        return if (pos != null) className.substring(0, pos) else ""
    }
}