package io.github.raghavsatyadev.moreapps.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun Context.getConString(
    @StringRes
    stringId: Int,
): String {
    return getString(stringId)
}

fun Context.getConDrawable(
    @DrawableRes
    drawableId: Int,
): Drawable? {
    return ContextCompat.getDrawable(this, drawableId)
}

fun Context.getConColor(
    @ColorRes
    colorId: Int,
): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.getAttrColorString(attributeName: String?): String {
    val outValue = TypedValue()
    val appCompatAttribute =
        resources.getIdentifier(attributeName, "attr", packageName)
    theme.resolveAttribute(appCompatAttribute, outValue, true)
    return String.format("#%06X", 0xFFFFFF and outValue.data)
}

fun Context.getColorPrimaryInHex(): String {
    return this.getAttrColorString("colorPrimary")
}

fun Context.getColorOnPrimaryColorInHex(): String {
    return getAttrColorString("colorOnPrimary")
}