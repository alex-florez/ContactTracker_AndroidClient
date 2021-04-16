package es.uniovi.eii.contacttracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Modulo de dependencias Network, que contiene todas las
 * dependencias relacionadas con la conexión con el BackEnd.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // API Rest para notificar y registrar POSITIVOS
    @Provides
    @Singleton
    fun providePositiveAPI(): PositiveAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_LOCALHOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PositiveAPI::class.java)
    }
}