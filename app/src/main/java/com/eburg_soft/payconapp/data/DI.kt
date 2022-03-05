package com.eburg_soft.payconapp.data

import com.eburg_soft.payconapp.data.network.PayconApi
import com.eburg_soft.payconapp.data.repositories.PayconRepositoryImpl
import com.eburg_soft.payconapp.domain.repositories.PayconRepository
import com.eburg_soft.payconapp.presentation.screens.viewModels.goods.GoodsViewModel
import com.eburg_soft.payconapp.presentation.screens.viewModels.goods.GoodsViewModelFactory
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

val apiModule = DI.Module("api") {
    bind<NetworkUtils>() with singleton { NetworkUtils() }
    bind<PayconApi>() with singleton { PayconApi.invoke() }
}

val repositoriesModule = DI.Module("repositories") {
    import(apiModule)
    bind<PayconRepository>() with singleton { PayconRepositoryImpl(instance()) }
}

val viewModelsModule = DI.Module("viewModels") {
    bind() from provider { GoodsViewModelFactory(instance(), instance()) }
    bind<GoodsViewModel>() with factory { GoodsViewModel(instance(), instance()) }
}