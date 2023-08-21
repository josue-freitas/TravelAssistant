package pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingAPI {

    @GET("json")
    suspend fun getReverseGeocoding(
        @Query("latlng") latlng: String,
        @Query("key") key: String = "AIzaSyDko83WUIktst92Sp3z7YOVJDUWUOLVVY0",
    ): Response<ReverseGeocodingResponse>

}