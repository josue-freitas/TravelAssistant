package pt.ipp.estg.myapplication.models.retrofit.routes

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RoutesAPI {

    @GET("json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String = "AIzaSyDko83WUIktst92Sp3z7YOVJDUWUOLVVY0",
    ): Response<RoutesResponse>

}