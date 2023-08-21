package pt.ipp.estg.myapplication.models.retrofit.charge_station

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ChargerStationAPI {

    @Headers("x-api-key: f4064840-43d7-482f-a91a-17fb78430dd8")
    @GET("poi")
    suspend fun getChargeStations(
        @Query("distanceunit") distanceunit: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("distance") distance: Int
    ): Response<ChargeStationResponse>

}