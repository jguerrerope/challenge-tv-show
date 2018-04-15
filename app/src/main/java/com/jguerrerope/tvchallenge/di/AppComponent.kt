package com.jguerrerope.tvchallenge.di

import com.jguerrerope.tvchallenge.TvShowApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            (AndroidInjectionModule::class),
            (AppModule::class),
            (MainActivityModule::class)
        ]
)
abstract class AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: TvShowApplication): Builder

        fun build(): AppComponent
    }

    abstract fun inject(app: TvShowApplication)
}