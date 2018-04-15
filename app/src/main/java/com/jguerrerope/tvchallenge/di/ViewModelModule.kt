package com.jguerrerope.tvchallenge.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jguerrerope.tvchallenge.ui.viewmodel.TvShowPopularViewModel
import com.jguerrerope.tvchallenge.ui.viewmodel.TvShowViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TvShowPopularViewModel::class)
    abstract fun bindWorkViewModel(viewModel: TvShowPopularViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: TvShowViewModelFactory): ViewModelProvider.Factory
}