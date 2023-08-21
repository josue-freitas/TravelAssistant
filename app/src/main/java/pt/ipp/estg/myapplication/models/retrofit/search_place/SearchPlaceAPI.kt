package pt.ipp.estg.myapplication.models.retrofit.search_place

import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.ReverseGeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchPlaceAPI {

    @GET("json")
    suspend fun getPlace(
        @Query("query") latlng: String,
        @Query("key") key: String = "AIzaSyDko83WUIktst92Sp3z7YOVJDUWUOLVVY0",
    ): Response<SearchPlaceResponse>
}