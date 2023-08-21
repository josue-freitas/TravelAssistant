package pt.ipp.estg.myapplication.models.database.charge_station

import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.retrofit.charge_station.ChargeStationResponse
import pt.ipp.estg.myapplication.models.retrofit.charge_station.ChargerStationAPI
import retrofit2.Response

class ChargeStationRepository(
    private val restChargerStationAPI: ChargerStationAPI
) {
    suspend fun getChargerStations(
        locationDetails: LocationDetails?,
        distance: Int
    ): Response<ChargeStationResponse> {
        return this.restChargerStationAPI.getChargeStations(
            "km",
            locationDetails?.latitude.toString(),
            locationDetails?.longitude.toString(),
            distance
        )
    }
}