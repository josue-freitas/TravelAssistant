package pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelperReverseGeoCoding {

    val baseUrl = "https://maps.googleapis.com/maps/api/geocode/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}
