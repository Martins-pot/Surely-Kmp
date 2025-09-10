package com.sportmaster.surelykmp.di



import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
}