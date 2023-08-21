package pt.ipp.estg.myapplication.models.retrofit.gas_station

import pt.ipp.estg.myapplication.models.retrofit.GasStationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GasStationApi {

    @GET("PesquisarPostos")
    suspend fun getStations(
        @Query("idsTiposComb") idsTiposComb: Int,
        @Query("idDistrito") idDistrito: Int,
        @Query("qtdPorpagina") qtdPorpagina: Int = 500,
    ): Response<GasStationResponse>
}
