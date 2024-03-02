package com.binay.shaw.justap.di

import android.content.Context
import com.binay.shaw.justap.data.datastore.DataStoreUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDataStoreUtil(@ApplicationContext context: Context): DataStoreUtil =
        DataStoreUtil(context)
}