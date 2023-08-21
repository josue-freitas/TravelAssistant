package pt.ipp.estg.myapplication.models.database.vehicle

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ipp.estg.myapplication.enumerations.MaintenanceStatus
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes

@Entity
data class Vehicle(
    @PrimaryKey()
    val plate: String = "",

    val brand: String = "",
    val model: String = "",
    val vehicleType: VehiclesTypes = VehiclesTypes.AUTOMOBILE,
    val specification: String = "",
    val associationVehicle: String = "= ",
    val uriString: String? = null,

    val oilFilterKm: Int? = null,
    val cabinFilterKm: Int? = null,
    val frontBrakeShimsKm: Int? = null,
    val backBrakeShimsKm: Int? = null,
    val frontBrakeDisksKm: Int? = null,
    val backBrakeDisksKm: Int? = null,

    var maintenanceStatus: MaintenanceStatus? = null
) {
}

