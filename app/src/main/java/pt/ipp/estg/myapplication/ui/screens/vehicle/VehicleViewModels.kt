package pt.ipp.estg.myapplication.ui.screens.vehicle

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.*
import pt.ipp.estg.myapplication.enumerations.MaintenanceStatus
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import pt.ipp.estg.myapplication.models.database.vehicle.VehicleDatabase
import pt.ipp.estg.myapplication.models.database.vehicle.VehicleRepository
import pt.ipp.estg.myapplication.models.firebase.StorageRepository

class VehicleViewModels(application: Application) : AndroidViewModel(application) {
    private val vehicleRepository: VehicleRepository

    private val storageRepository: StorageRepository = StorageRepository()

    var allVehicles: LiveData<List<Vehicle>> = MutableLiveData()

    init {
        val db = VehicleDatabase.getDatabase(application)
        vehicleRepository = VehicleRepository(db.getVehicleDao())
        allVehicles = vehicleRepository.getVehicles()
    }

    //adds the maintenance status
    fun updateMaintenanceInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            allVehicles.value?.forEach { vehicle ->
                vehicle.maintenanceStatus = getMaintenanceStatus(vehicle)
            }
        }
    }

    fun getVehicleByPlate(plate: String): LiveData<Vehicle> {
        return vehicleRepository.getVehicleByPlate(plate)
    }

    fun insert(vehicle: Vehicle) {
        Log.d("vehicle", vehicle.toString())
        viewModelScope.launch(Dispatchers.IO) {
            vehicleRepository.insert(vehicle)
        }
    }

    fun delete(plate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleRepository.delete(plate)
        }
    }

    private fun replaceAllBy(newVehicles: List<Vehicle>?) {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleRepository.deleteAll()
            if (newVehicles != null) {
                for (item in newVehicles) {
                    vehicleRepository.insert(item)
                }
            }
        }
    }

    fun uploadVehiclesToCloudByUser(
        context: Context, email: String, vehicles: State<List<Vehicle>?>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            vehicles.value?.let {
                storageRepository.uploadVehiclesToUser(email, it) { result ->
                    if (result) {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.upload_vehicle_success),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    } else {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.upload_vehicle_error),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                }
            }
        }
    }

    fun getCloudVehicles(
        email: String,
        context: Context,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            storageRepository.getVehicles(email, onError = {
                FancyToast.makeText(
                    context,
                    context.resources.getString(R.string.get_vehicles_error),
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }, onSuccess = {
                if (it == null || it.isEmpty()) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_vehicles_empty),
                        FancyToast.LENGTH_LONG,
                        FancyToast.WARNING,
                        false
                    ).show()
                } else {
                    replaceAllBy(it)
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_vehicles_success),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
            })
        }
    }

    fun removeVehiclesInCloudByUser(
        context: Context, email: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            storageRepository.uploadVehiclesToUser(email, listOf()) { result ->
                if (result) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.remove_vehicles_success),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                } else {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.upload_vehicle_error),
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR,
                        false
                    ).show()
                }
            }
        }
    }

    /*
      Updates the kms of maintenance status, based on the kms traveled by the user.
     */
    fun registerDistance(plate: String, distance: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var vehicle = allVehicles.value?.filter { (it.plate == plate) }?.get(0)
            var oilFilterKm = 100
            var cabinFilterKm: Int? = null
            var frontBrakeShimsKm: Int? = null
            var backBrakeShimsKm: Int? = null
            var frontBrakeDisksKm: Int? = null
            var backBrakeDisksKm: Int? = null

            if (vehicle?.oilFilterKm != null) {
                oilFilterKm = if (vehicle?.oilFilterKm!! - distance < 0) {
                    0
                } else {
                    vehicle?.oilFilterKm!! - distance
                }
            }

            if (vehicle?.cabinFilterKm != null) {
                cabinFilterKm = if (vehicle?.cabinFilterKm!! - distance < 0) {
                    0
                } else {
                    vehicle?.cabinFilterKm!! - distance
                }
            }

            if (vehicle?.frontBrakeShimsKm != null) {
                frontBrakeShimsKm = if (vehicle?.frontBrakeShimsKm!! - distance < 0) {
                    0
                } else {
                    vehicle?.frontBrakeShimsKm!! - distance
                }
            }

            if (vehicle?.backBrakeShimsKm != null) {
                backBrakeShimsKm = if (vehicle?.backBrakeShimsKm!! - distance < 0) {
                    0
                } else {
                    vehicle?.backBrakeShimsKm!! - distance
                }
            }

            if (vehicle?.frontBrakeDisksKm != null) {
                frontBrakeDisksKm = if (vehicle?.frontBrakeDisksKm!! - distance < 0) {
                    0
                } else {
                    vehicle?.frontBrakeDisksKm!! - distance
                }
            }

            if (vehicle?.backBrakeDisksKm != null) {
                backBrakeDisksKm = if (vehicle?.backBrakeDisksKm!! - distance < 0) {
                    0
                } else {
                    vehicle?.backBrakeDisksKm!! - distance
                }
            }

            vehicleRepository.updateKms(
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

    private fun getMaintenanceStatus(vehicle: Vehicle): MaintenanceStatus {
        var countOfRegistered = 0
        var status = MaintenanceStatus.WITHOUT_DATA

        //CHECK OIL
        if (vehicle.oilFilterKm != null) {
            countOfRegistered++
            if (vehicle.oilFilterKm < MIN_KM_OIL) {
                return MaintenanceStatus.DANGER
            } else if (vehicle.oilFilterKm < MEDIUM_KM_OIL) {
                status = MaintenanceStatus.WARNING
            }
        }

        //CHECK CABIN
        if (vehicle.cabinFilterKm != null) {
            countOfRegistered++
            if (vehicle.cabinFilterKm < MIN_KM_CABIN) {
                return MaintenanceStatus.DANGER
            } else if (vehicle.cabinFilterKm < MEDIUM_KM_CABIN) {
                status = MaintenanceStatus.WARNING
            }
        }

        //CHECK FRONT SHIMS
        if (vehicle.frontBrakeShimsKm != null) {
            countOfRegistered++
            if (vehicle.frontBrakeShimsKm < MIN_KM_SHIMS) {
                return MaintenanceStatus.DANGER
            } else if (vehicle.frontBrakeShimsKm < MEDIUM_KM_SHIMS) {
                status = MaintenanceStatus.WARNING
            }
        }

        //CHECK FRONT SHIMS
        if (vehicle.backBrakeShimsKm != null) {
            countOfRegistered++
            if (vehicle.backBrakeShimsKm < MIN_KM_SHIMS) {
                return MaintenanceStatus.DANGER
            } else if (vehicle.backBrakeShimsKm < MEDIUM_KM_SHIMS) {
                status = MaintenanceStatus.WARNING
            }
        }

        //CHECK FRONT DISKS
        if (vehicle.frontBrakeDisksKm != null) {
            countOfRegistered++
            if (vehicle.frontBrakeDisksKm < MIN_KM_DISKS) {
                return MaintenanceStatus.DANGER
            } else if (vehicle.frontBrakeDisksKm < MEDIUM_KM_DISKS) {
                status = MaintenanceStatus.WARNING
            }
        }

        //CHECK BACK DISKS
        if (vehicle.backBrakeDisksKm != null) {
            countOfRegistered++
            if (vehicle.backBrakeDisksKm < MIN_KM_DISKS) {
                return MaintenanceStatus.DANGER
            } else if (vehicle.backBrakeDisksKm < MEDIUM_KM_DISKS) {
                status = MaintenanceStatus.WARNING
            }
        }

        //is nothing was wrong, check is there was no info ou everything was ok
        if (countOfRegistered > 0 && status != MaintenanceStatus.WARNING) {
            status = MaintenanceStatus.OK
        }
        return status
    }
}