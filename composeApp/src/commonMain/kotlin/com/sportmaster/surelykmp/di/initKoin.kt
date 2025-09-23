package com.sportmaster.surelykmp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
//        appDeclaration()
        config?.invoke(this)
        modules(sharedModule, platformModule)
    }
}