package com.example.trainbookingsystem.di

import android.content.Context
import com.example.trainbookingsystem.util.UsersManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUsersManager(@ApplicationContext context: Context): UsersManager =
        UsersManager(context)

}