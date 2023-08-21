package pt.ipp.estg.myapplication.models.retrofit

import pt.ipp.estg.myapplication.models.retrofit.gas_station.GasStationData

data class GasStationResponse(
    val resultado: List<GasStationData>,
)