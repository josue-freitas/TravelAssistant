package pt.ipp.estg.myapplication.models.database.gas_station

import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.enumerations.GasType
import pt.ipp.estg.myapplication.enumerations.getIdByName
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.retrofit.*
import pt.ipp.estg.myapplication.models.retrofit.gas_station.GasStationApi
import pt.ipp.estg.myapplication.models.retrofit.gas_station.GasStationDao
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.ReverseGeocodingAPI
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.ReverseGeocodingResponse
import retrofit2.Response

class GasStationRepository(
    private val gasStationDao: GasStationDao,
    private val restAPIGasStation: GasStationApi,
    private val restAPIReverseGeocoding: ReverseGeocodingAPI,

    ) {
    fun getStations(
        gasType: GasType,
        rangeDistance: Int,
        orderBy: String
    ): MutableList<GasStation> {
        if (orderBy == "Distance") {
            return gasStationDao.getGasStationsByDistance(
                gasType.nameOnAPI,
                rangeDistance
            )
        }
        return gasStationDao.getGasStationsByPrice(
            gasType.nameOnAPI,
            rangeDistance
        )

    }

    suspend fun getGasStationsOnline(
        gasType: GasType,
        districtName: String
    ): Response<GasStationResponse> {
        return this.restAPIGasStation.getStations(gasType.id, getIdByName(districtName))
    }

    suspend fun getDistrict(locationDetails: LocationDetails?): Response<ReverseGeocodingResponse> {
        return this.restAPIReverseGeocoding.getReverseGeocoding(
            "${locationDetails?.latitude.toString()},${locationDetails?.longitude.toString()}",
            "AIzaSyDko83WUIktst92Sp3z7YOVJDUWUOLVVY0"
        )
    }

    suspend fun insertGasStation(gasStation: GasStation) {
        gasStationDao.insertGasStation(gasStation)
    }

    fun deleteGasStations() {
        gasStationDao.deleteGasStations()
    }

    fun updateDistanceAndCoupons(
        id: Int,
        combustivel: String,
        distance: Double,
        precoComDesconto: Float,
        tipoDesconto: CouponType
    ) {
        gasStationDao.updateDistanceAndCoupons(
            id,
            combustivel,
            distance,
            precoComDesconto,
            tipoDesconto
        )
    }
}
