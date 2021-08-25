package es.uniovi.eii.contacttracker.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.BuildConfig
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.network.api.ConfigAPI
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.api.StatisticsAPI
import es.uniovi.eii.contacttracker.network.converters.PointConverter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/* Formato String de las fechas */
const val DATE_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss"

/**
 * Modulo de dependencias Network, que contiene todas las
 * dependencias relacionadas con la conexión con el BackEnd.
 */
@Module
@InstallIn(SingletonComponent::class)
object APIModule {

    /* API Rest para notificar y registrar POSITIVOS */
    @Provides
    @Singleton
    fun providePositiveAPI(): PositiveAPI {
        /* Custom GSON Serializer/Deserializer */
        val gson = GsonBuilder()
            .registerTypeAdapter(Point::class.java, PointConverter())
            .setDateFormat(DATE_STRING_FORMAT)
            .create()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PositiveAPI::class.java)
    }

    /* API Rest para obtener los datos de Configuración */
    @Provides
    @Singleton
    fun provideConfigAPI(): ConfigAPI {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ConfigAPI::class.java)
    }

    /* API Rest para las estadísticas de la aplicación */
    @Provides
    @Singleton
    fun provideStatisticsAPI(): StatisticsAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StatisticsAPI::class.java)
    }
}