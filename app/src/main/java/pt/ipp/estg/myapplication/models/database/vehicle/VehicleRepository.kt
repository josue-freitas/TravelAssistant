package pt.ipp.estg.myapplication.models.database.vehicle

import androidx.lifecycle.LiveData

class VehicleRepository(val vehicleDao: VehicleDao) {

    fun getVehicles(): LiveData<List<Vehicle>> {
        return vehicleDao.getVehicles()
    }

    fun getVehicleByPlate(plate: String): LiveData<Vehicle> {
        return vehicleDao.getVehicleByPlate(plate)
    }

    suspend fun insert(vehicle: Vehicle) {
        vehicleDao.insert(vehicle)
    }

    suspend fun delete(plate: String) {
        vehicleDao.delete(plate)
    }

    suspend fun deleteAll(){
        vehicleDao.deleteAll()
    }


    suspend fun updateKms(
        plate: String,
        oilFilterKm: Int?,
        cabinFilterKm: Int?,
        frontBrakeShimsKm: Int?,
        backBrakeShimsKm: Int?,
        frontBrakeDisksKm: Int?,
        backBrakeDisksKm: Int?
    ) {
        vehicleDao.updateKms(
            plate,
            oilFilterKm,
            cabinFilterKm,
            frontBrakeShimsKm,
            backBrakeShimsKm,
            frontBrakeDisksKm,
            backBrakeDisksKm
        )
    }
}
