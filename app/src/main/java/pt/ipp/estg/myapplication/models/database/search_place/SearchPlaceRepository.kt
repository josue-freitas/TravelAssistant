package pt.ipp.estg.myapplication.models.database.search_place

import pt.ipp.estg.myapplication.models.retrofit.search_place.SearchPlaceAPI
import pt.ipp.estg.myapplication.models.retrofit.search_place.SearchPlaceResponse
import retrofit2.Response

class SearchPlaceRepository(
    private val searchPlaceAPI: SearchPlaceAPI
) {

    suspend fun getSearch(
        place: String
    ): Response<SearchPlaceResponse> {
        return this.searchPlaceAPI.getPlace(
            place
        )
    }
}