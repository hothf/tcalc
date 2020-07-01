package de.ka.jamit.arch.utils.resources

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Provides accessors for resources. Used to not have to use the context as parameter, it is injected instead.
 */
interface ResourcesProvider {

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg args: Any?): String
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg args: Any?): String
    fun getColor(resId: Int): Int
    fun getDrawable(resId: Int): Drawable?
    fun getDimensionPixelSize(resId: Int): Int
    fun getInteger(resId: Int): Int
    fun getApplicationContext(): Context
}
