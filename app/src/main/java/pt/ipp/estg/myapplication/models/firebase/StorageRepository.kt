package pt.ipp.estg.myapplication.models.firebase

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.enumerations.GasType
import pt.ipp.estg.myapplication.enumerations.MaintenanceStatus
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.models.database.coupon.Coupon
import pt.ipp.estg.myapplication.models.database.coupon.GasTypeList
import pt.ipp.estg.myapplication.models.database.user.User
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import java.util.Objects
import kotlin.reflect.typeOf

const val VEHICLE_COLLECTION = "vehicles"
const val COUPONS_COLLECTION = "coupons"
const val LOCATIONS_COLLECTION = "locations"

class StorageRepository() {
    private val vehicleRef: CollectionReference = Firebase.firestore.collection(VEHICLE_COLLECTION)
    private val couponsRef: CollectionReference = Firebase.firestore.collection(COUPONS_COLLECTION)
    private val locationsRef: CollectionReference =
        Firebase.firestore.collection(LOCATIONS_COLLECTION)

    fun uploadVehiclesToUser(
        email: String, vehicles: List<Vehicle>, onComplete: (Boolean) -> Unit,
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map["vehicles"] = vehicles
        vehicleRef
            .document(email)
            .set(map)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun uploadCouponsToUser(
        email: String, coupons: List<Coupon>, onComplete: (Boolean) -> Unit,
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map["coupons"] = coupons
        couponsRef
            .document(email)
            .set(map)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun uploadLocationsToUser(
        email: String,
        homeLocations: LatLng,
        workLocations: LatLng,
        onComplete: (Boolean) -> Unit,
    ) {
        val map: MutableMap<String, HashMap<String, HashMap<String, String>>> = HashMap()
        val mapLocation: HashMap<String, HashMap<String, String>> = HashMap()

        val subMapLocationsHome: HashMap<String, String> = HashMap()
        if (homeLocations != null) {
            subMapLocationsHome["lat"] = homeLocations.latitude.toString()
            subMapLocationsHome["long"] = homeLocations.longitude.toString()
        }

        val subMapLocationsWork: HashMap<String, String> = HashMap()
        if (workLocations != null) {
            subMapLocationsWork["lat"] = workLocations.latitude.toString()
            subMapLocationsWork["long"] = workLocations.longitude.toString()
        }

        mapLocation["home"] = subMapLocationsHome
        mapLocation["work"] = subMapLocationsWork

        map["locations"] = mapLocation

        locationsRef
            .document(email)
            .set(map)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun getVehicles(
        email: String,
        onError: (Exception) -> Unit,
        onSuccess: (List<Vehicle>?) -> Unit
    ) {
        vehicleRef
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                Log.d("vehicles cloud", "data ${document.data?.get("vehicles").toString()}")

                if (!document.exists()) {
                    onSuccess.invoke(null)
                } else {
                    val map = document.data as HashMap<String, List<HashMap<String, Any>>>

                    val list = map["vehicles"]

                    val newList = mutableListOf<Vehicle>()
                    if (list != null) {
                        for (item in list) {
                            val oilFilterKm =
                                if (item["oilFilterKm"] != null) (item["oilFilterKm"] as Long).toInt() else null
                            val cabinFilterKm =
                                if (item["cabinFilterKm"] != null) (item["cabinFilterKm"] as Long).toInt() else null
                            val frontBrakeShimsKm =
                                if (item["frontBrakeShimsKm"] != null) (item["frontBrakeShimsKm"] as Long).toInt() else null
                            val backBrakeShimsKm =
                                if (item["backBrakeShimsKm"] != null) (item["backBrakeShimsKm"] as Long).toInt() else null
                            val frontBrakeDisksKm =
                                if (item["frontBrakeDisksKm"] != null) (item["frontBrakeDisksKm"] as Long).toInt() else null
                            val backBrakeDisksKm =
                                if (item["backBrakeDisksKm"] != null) (item["backBrakeDisksKm"] as Long).toInt() else null


                            val vehicle = Vehicle(
                                plate = item["plate"] as String,
                                brand = item["brand"] as String,
                                model = item["model"] as String,
                                vehicleType = VehiclesTypes.valueOf(item["vehicleType"] as String),
                                specification = item["specification"] as String,
                                associationVehicle = item["associationVehicle"] as String,
                                uriString = item["uriString"] as String,
                                oilFilterKm = oilFilterKm,
                                cabinFilterKm = cabinFilterKm,
                                frontBrakeShimsKm = frontBrakeShimsKm,
                                backBrakeShimsKm = backBrakeShimsKm,
                                frontBrakeDisksKm = frontBrakeDisksKm,
                                backBrakeDisksKm = backBrakeDisksKm
                            )
                            newList.add(vehicle)
                        }
                    }
                    Log.d("vehicles cloud", "list ${map["vehicles"].toString()}")
                    onSuccess.invoke(newList)
                }
            }
            .addOnFailureListener {
                Log.e("cloud", "$it")
                onError.invoke(it)
            }
    }

    fun getCoupons(
        email: String,
        onError: (Exception) -> Unit,
        onSuccess: (List<Coupon>?) -> Unit
    ) {
        couponsRef
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                Log.d("locations cloud", "data ${document.data?.get("locations").toString()}")

                if (!document.exists()) {
                    onSuccess.invoke(null)
                } else {
                    val map = document.data as HashMap<String, List<HashMap<String, Any>>>

                    val list = map["coupons"]

                    val newList = mutableListOf<Coupon>()
                    if (list != null) {
                        for (item in list) {
                            val brandGasStation = item["brandGasStation"] as String
                            val discountVale = (item["couponId"] as Long).toFloat()
                            val type = CouponType.valueOf(item["type"] as String)
                            val minimalLiters =
                                if (item["minimalLiters"] != null) (item["minimalLiters"] as Long).toInt() else null
                            val maxLiters =
                                if (item["minimalLiters"] != null) (item["minimalLiters"] as Long).toInt() else null
                            val startDate =
                                if (item["startDate"] != null) (item["startDate"] as String) else null
                            val endDate =
                                if (item["endDate"] != null) (item["endDate"] as String) else null

                            val gasTypeMap =
                                item["gasTypesApplied"] as HashMap<String, List<String>>
                            val gasTypeList = gasTypeMap["gasTypeApplied"]

                            val gasTypeCleanList = mutableListOf<GasType>()

                            if (gasTypeList != null) {
                                for (stringGas in gasTypeList) {
                                    if (stringGas == "SIMPLEDIESEL") {
                                        gasTypeCleanList.add(GasType.SIMPLEDIESEL)
                                    }
                                    if (stringGas == "LPG") {
                                        gasTypeCleanList.add(GasType.LPG)
                                    }
                                    if (stringGas == "SIMPLEGASOLINE95") {
                                        gasTypeCleanList.add(GasType.SIMPLEGASOLINE95)
                                    }
                                    if (stringGas == "SIMPLEGASOLINE98") {
                                        gasTypeCleanList.add(GasType.SIMPLEGASOLINE98)
                                    }
                                }
                            }

                            val gasTypesApplied = GasTypeList(gasTypeCleanList)

                            val coupon = Coupon(
                                type = type,
                                brandGasStation = brandGasStation,
                                endDate = endDate,
                                startDate = startDate,
                                maxLiters = maxLiters,
                                gasTypesApplied = gasTypesApplied,
                                minimalLiters = minimalLiters,
                                discountVale = discountVale,
                                valid = null,
                            )
                            newList.add(coupon)
                        }
                    }
                    Log.d("coupon cloud", "list ${map["coupon"].toString()}")
                    onSuccess.invoke(newList)
                }
            }
            .addOnFailureListener {
                Log.e("cloud", "$it")
                onError.invoke(it)
            }
    }

    fun getLocations(
        email: String,
        onError: (Exception) -> Unit,
        onSuccess: (home: LatLng?, work: LatLng?) -> Unit
    ) {
        locationsRef
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                Log.d("locations cloud", "data ${document.data?.get("locations").toString()}")
                if (!document.exists()) {
                    onSuccess.invoke(null, null)
                } else {
                    val map =
                        document.data as MutableMap<String, HashMap<String, HashMap<String, String>>>

                    val couponsMap = map["locations"]

                    val homeObj = couponsMap?.get("home")
                    val workObj = couponsMap?.get("work")

                    var homeLocation: LatLng? = null
                    if (homeObj != null) {
                        homeLocation =
                            LatLng(
                                (homeObj["lat"] as String).toDouble(),
                                (homeObj["long"] as String).toDouble(),
                            )
                    }

                    var workLocation: LatLng? = null
                    if (workObj != null) {
                        workLocation =
                            LatLng(
                                (workObj["lat"] as String).toDouble(),
                                (workObj["long"] as String).toDouble(),
                            )
                    }

                    onSuccess.invoke(homeLocation, workLocation)
                }
            }
            .addOnFailureListener {
                Log.e("cloud", "$it")
                onError.invoke(it)
            }
    }

}