package com.eburg_soft.payconapp.presentation

import android.app.Application
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule

open class App : Application(), DIAware {

    override val di by DI.lazy {
        import(androidXModule(this@App))
    }


}