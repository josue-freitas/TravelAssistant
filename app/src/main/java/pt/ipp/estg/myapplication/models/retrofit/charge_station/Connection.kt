package pt.ipp.estg.myapplication.models.retrofit.charge_station

data class Connection(
    val Amps: Int,
    val ConnectionType: ConnectionType,
    val CurrentType: CurrentType,
    val PowerKW: Double,
    val Voltage: Int
)