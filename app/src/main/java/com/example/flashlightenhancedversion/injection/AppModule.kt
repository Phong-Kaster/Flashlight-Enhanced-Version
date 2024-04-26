package com.example.flashlightenhancedversion.injection

import android.content.Context
import com.example.flashlightenhancedversion.FlashlightApplication
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
    fun provideApplication(@ApplicationContext context: Context): FlashlightApplication {
        return context as FlashlightApplication
    }
}