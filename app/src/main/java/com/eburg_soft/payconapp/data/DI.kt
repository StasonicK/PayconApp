package com.eburg_soft.payconapp.data

import com.eburg_soft.payconapp.data.network.PayconApi
import com.eburg_soft.payconapp.data.repositories.PayconRepositoryImpl
import com.eburg_soft.payconapp.domain.repositories.PayconRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.singleton

val apiModule = DI.Module("api") {
    bindSingleton { PayconApi }
}

val repositoriesModule = DI.Module("repositories") {
    import(apiModule)
    bind<PayconRepository>() with singleton { PayconRepositoryImpl() }
}