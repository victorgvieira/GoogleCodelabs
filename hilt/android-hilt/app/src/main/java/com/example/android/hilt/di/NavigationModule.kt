package com.example.android.hilt.di

import com.example.android.hilt.navigator.AppNavigator
import com.example.android.hilt.navigator.AppNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

// DONE since it will use Activity information, install in ActivityComponent
@InstallIn(ActivityComponent::class)
// DONE make this class a Module
@Module
abstract class NavigationModule {
    // DONE annotate Binds to an abstract function
    //  the return type must be the interface
    //  the UNIQUE parameter must be the interface implementation
    @Binds
    abstract fun bindNavigator(impl: AppNavigatorImpl): AppNavigator
}