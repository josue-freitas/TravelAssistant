package pt.ipp.estg.myapplication.models.retrofit.charge_station

data class ChargeStationResponseItem(
    val OperatorInfo: OperatorInfo,
    val AddressInfo: AddressInfo,
    val Connections: List<Connection>,
    val NumberOfPoints: Int,
)