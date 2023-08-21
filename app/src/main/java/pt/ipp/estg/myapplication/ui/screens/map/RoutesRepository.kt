package pt.ipp.estg.myapplication.ui.screens.map

import pt.ipp.estg.myapplication.models.retrofit.routes.RoutesAPI
import pt.ipp.estg.myapplication.models.retrofit.routes.RoutesResponse
import retrofit2.Response

class RoutesRepository(
    private val routesAPI: RoutesAPI
) {
    suspend fun getRoutesAPI(
        origin: String,
        destination: String
    ): Response<RoutesResponse> {
        return this.routesAPI.getRoute(
            origin, destination
        )
    }
}