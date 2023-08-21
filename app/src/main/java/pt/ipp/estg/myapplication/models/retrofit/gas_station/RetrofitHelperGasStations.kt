package pt.ipp.estg.myapplication.models.retrofit.gas_station

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelperGasStations {

    val baseUrl = "http://precoscombustiveis.dgeg.gov.pt/api/PrecoComb/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}
