package com.eburg_soft.payconapp.presentation.base

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : Fragment> T.withArgs(args: Parcelable) = apply { arguments = bundleOf("fragment_args" to args) }

inline fun <reified T> arguments(defaultValue: T? = null): ReadWriteProperty<Fragment, T> =
    BundleExtractorDelegate { thisRef -> extractFromBundle(thisRef.arguments, "fragment_args", defaultValue) }

inline fun <reified T> extractFromBundle(bundle: Bundle?, key: String, defaultValue: T? = null): T {
    val result = bundle?.get(key) ?: defaultValue
    if (result != null && result !is T) throw ClassCastException("Property $key has different class type")
    return result as T
}

class BundleExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == EMPTY) value = initializer(thisRef)
        return value as T
    }
}