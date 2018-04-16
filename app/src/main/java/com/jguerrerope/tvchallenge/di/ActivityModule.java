package com.jguerrerope.tvchallenge.di;

import com.jguerrerope.tvchallenge.ui.TvShowDetailsActivity;
import com.jguerrerope.tvchallenge.ui.TvShowListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract TvShowListActivity contributeTvShowListActivity();

    @ContributesAndroidInjector
    abstract TvShowDetailsActivity contributeTvShowDetailsActivity();
}
