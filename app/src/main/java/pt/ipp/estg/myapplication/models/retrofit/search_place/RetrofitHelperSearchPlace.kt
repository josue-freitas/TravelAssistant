package pt.ipp.estg.myapplication.models.retrofit.search_place

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelperSearchPlace {
    val baseUrl = "https://maps.googleapis.com/maps/api/place/textsearch/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}