package pt.ipp.estg.myapplication.models.database.gas_station

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.enumerations.GasType
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.database.coupon.Coupon
import pt.ipp.estg.myapplication.models.retrofit.gas_station.GasStationApi
import pt.ipp.estg.myapplication.models.retrofit.gas_station.RetrofitHelperGasStations
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.RetrofitHelperReverseGeoCoding
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.ReverseGeocodingAPI
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*

@Suppress("NAME_SHADOWING")
class GasStationViewModels(application: Application) : AndroidViewModel(application) {

    private val repository: GasStationRepository
    var gasStations: MutableLiveData<MutableList<GasStation>> = MutableLiveData()
    private var districtName: MutableLiveData<String> = MutableLiveData()
    private lateinit var couponsList: State<List<Coupon>?>

    @SuppressLint("StaticFieldLeak")
    var context: Context

    init {
        val db = GasStationDatabase.getDatabase(application)
        val restAPIGasStation =
            RetrofitHelperGasStations.getInstance().create(GasStationApi::class.java)
        val restAPIReverseGeocoding =
            RetrofitHelperReverseGeoCoding.getInstance().create(ReverseGeocodingAPI::class.java)
        repository =
            GasStationRepository(db.getGasStationDao(), restAPIGasStation, restAPIReverseGeocoding)
        context = application.baseContext
    }

    fun onInit(coupons: State<List<Coupon>?>) {
        couponsList = coupons
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getAllGasStations(currentLocation: LocationDetails?) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deleteGasStations()
            withContext(Dispatchers.Default) {
                getDistrict(
                    currentLocation
                )
            }

            withContext(Dispatchers.Main) {
                val gasStationsPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
                val editor = gasStationsPref.edit()

                val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                val currentDate = LocalDateTime.now().format(formatter)

                editor.apply {
                    putString("lastDistrict", districtName.value)
                    putString("lastUpdateDate", currentDate)
                    apply()
                }

                updateGasStations(
                    GasType.SIMPLEDIESEL, districtName.value.toString(), currentLocation
                )

                updateGasStations(
                    GasType.SIMPLEGASOLINE95, districtName.value.toString(), currentLocation
                )
                updateGasStations(
                    GasType.SIMPLEGASOLINE98, districtName.value.toString(), currentLocation
                )
                updateGasStations(GasType.LPG, districtName.value.toString(), currentLocation)

            }
        }
    }


    fun getGasStationsOffline(
        gasType: GasType,
        rangeDistance: Int,
        orderBy: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            gasStations.postValue(repository.getStations(gasType, rangeDistance, orderBy))
        }
    }

    private fun insertGasStation(gasStation: GasStation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGasStation(gasStation)
        }
    }

    private suspend fun getDistrict(locationDetails: LocationDetails?) {
        try {
            val response = repository.getDistrict(locationDetails)
            if (response.isSuccessful) {
                val locationDetails =
                    response.body()?.results?.get(0)?.address_components?.get(2)
                val locationDetails2 =
                    response.body()?.results?.get(0)?.address_components?.get(3)
                if (locationDetails2?.types?.get(0) == "administrative_area_level_1") {
                    districtName.postValue(locationDetails2.long_name)
                } else if (locationDetails?.types?.get(0) != null) {
                    districtName.postValue(locationDetails.long_name)
                }
            }
        } catch (ce: CancellationException) {
            throw ce // Needed for coroutine scope cancellation
        } catch (e: Exception) {
            //TODO toast!
        }
    }

    private fun updateGasStations(
        gasType: GasType,
        district: String,
        currentLocation: LocationDetails?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getGasStationsOnline(gasType, district)
                if (response.isSuccessful) {
                    val content = response.body()
                    content?.resultado?.forEach {
                        if (currentLocation != null) {
                            val (price, discountType) = calculatePriceWithDiscount(
                                it.Marca,
                                gasType,
                                it.Preco.split(" ")[0].replace(",", ".").toFloat()
                            )
                            insertGasStation(
                                GasStation(
                                    it.Combustivel,
                                    it.Distrito,
                                    it.Id,
                                    it.Latitude,
                                    it.Localidade,
                                    it.Longitude,
                                    it.Marca,
                                    it.Morada,
                                    it.Municipio,
                                    it.Preco.split(" ")[0].replace(",", ".").toFloat(),
                                    price,
                                    discountType,
                                    calculateByDistance(
                                        LatLng(it.Latitude, it.Longitude),
                                        LatLng(
                                            currentLocation.latitude,
                                            currentLocation.longitude
                                        )
                                    )
                                )
                            )
                        }
                    }
                } else {
                    Log.e("ERROR API REQUEST", "ERROR")
                }
            } catch (ce: CancellationException) {
                Log.e("ERROR API REQUEST", ce.toString())
                throw ce // Needed for coroutine scope cancellation
            } catch (e: Exception) {
                Log.e("ERROR API REQUEST", e.toString())
                //TODO toast!
            }
        }
    }

    private fun calculateByDistance(StartP: LatLng, EndP: LatLng): Double {
        val radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(radius * c).replace(",", ".").toDouble()
    }

    private fun calculatePriceWithDiscount(
        brand: String,
        gasType: GasType,
        price: Float
    ): Pair<Float, CouponType?> {
        var priceWithDiscount = price
        var couponTypeApplied: CouponType? = null
        couponsList.value?.forEach { coupon ->
            if (coupon.brandGasStation == brand && coupon.gasTypesApplied.gasTypeApplied.contains(
                    gasType
                )
            ) {
                if (coupon.type == CouponType.PERLITER) {
                    priceWithDiscount =
                        if ((price - coupon.discountVale) > 0) ((floor(((price - coupon.discountVale) * 1000).toDouble()) / 1000).toFloat()) else 0.0f
                }
                couponTypeApplied = coupon.type
            }
        }
        return Pair(priceWithDiscount, couponTypeApplied)
    }
}