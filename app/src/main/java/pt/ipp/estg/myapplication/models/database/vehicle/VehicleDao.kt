package pt.ipp.estg.myapplication.models.database.vehicle

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VehicleDao {

    @Query("SELECT * FROM Vehicle")
    fun getVehicles(): LiveData<List<Vehicle>>

    @Query("SELECT * FROM Vehicle WHERE plate = :plate")
    fun getVehicleByPlate(plate: String): LiveData<Vehicle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Query("DELETE FROM Vehicle WHERE plate = :plate")
    suspend fun delete(plate: String)

    @Query("DELETE FROM Vehicle")
    suspend fun deleteAll()

    @Query("UPDATE Vehicle SET oilFilterKm = :newOilFilterKm, cabinFilterKm = :newCabinFilterKm, frontBrakeShimsKm = :newFrontBrakeShimsKm, backBrakeShimsKm = :newBackBrakeShimsKm, frontBrakeDisksKm = :newFrontBrakeDisksKm, backBrakeDisksKm = :newBackBrakeDisksKm WHERE plate = :plate")
    suspend fun updateKms(
        plate: String,
        newOilFilterKm: Int?,
        newCabinFilterKm: Int?,
        newFrontBrakeShimsKm: Int?,
        newBackBrakeShimsKm: Int?,
        newFrontBrakeDisksKm: Int?,
        newBackBrakeDisksKm: Int?
    )
}