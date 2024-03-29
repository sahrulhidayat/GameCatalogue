package com.sahrulhidayat.home.di

import com.sahrulhidayat.core.utils.DispatcherProvider
import com.sahrulhidayat.home.ui.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(DispatcherProvider.main(), get()) }
}