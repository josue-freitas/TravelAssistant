package pt.ipp.estg.myapplication.models

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.connection.NetworkConnectivityObserver
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.models.retrofit.routes.RetrofitHelpRoutes
import pt.ipp.estg.myapplication.models.retrofit.routes.RoutesAPI
import pt.ipp.estg.myapplication.ui.screens.map.RoutesRepository

class GeneralViewModels(application: Application) : AndroidViewModel(application) {

    private val repository: RoutesRepository

    val status = MutableLiveData<ConnectivityObserver.Status>()
    var marker = MutableLiveData<LatLng>()
    var textMarket = MutableLiveData<String>()
    val mapMode = MutableLiveData(MapMode.NORMAL)
    val routesDetails = MutableLiveData<List<LatLng>>()
    val destinationRoute =  MutableLiveData<LatLng>()

    @SuppressLint("StaticFieldLeak")
    var context: Context

    init {
        val connectivityObserver: ConnectivityObserver =
            NetworkConnectivityObserver(application.baseContext)
        context = application.baseContext
        val restAPIRoutes = RetrofitHelpRoutes.getInstance().create(
            RoutesAPI::class.java
        )
        repository = RoutesRepository(restAPIRoutes)

        viewModelScope.launch(Dispatchers.IO) {
            connectivityObserver.observe().collect {
                status.postValue(it)
            }
        }
    }

    fun addMarker(location: LatLng) {
        marker.value = location
    }

    fun setTextMarker(text: String) {
        textMarket.value = text
    }

    fun addDestinationRoute(location : LatLng){
        destinationRoute.value = location
    }

    fun setMapMode(mode: MapMode) {
        mapMode.value = mode
    }

    fun getRoute(location: LocationDetails?, destinationLocation: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getRoutesAPI(
                    location?.latitude.toString() + ", " + location?.longitude.toString(),
                    destinationLocation.latitude.toString() + ", " + destinationLocation.longitude.toString()
                )
                if (response.isSuccessful) {
                    val mutableList = mutableListOf<LatLng>()
                    response.body()?.routes?.get(0)?.legs?.get(0)?.steps?.forEach {
                        mutableList.add(LatLng(it.start_location.lat, it.start_location.lng))
                        mutableList.add(LatLng(it.end_location.lat, it.end_location.lng))

                    }
                    routesDetails.postValue(mutableList)
                } else {
                    Log.e("ERROR REQUEST API", response.toString())
                    Log.e("ERROR REQUEST API", response.message())
                }
            } catch (ce: CancellationException) {
                Log.e("EXCEPTION", ce.toString())
                throw ce // Needed for coroutine scope cancellation
            } catch (e: Exception) {
                Log.e("EXCEPTION", e.toString())
                //TODO toast!
            }
        }
    }

    fun registerDistance(vehicleViewModels: VehicleViewModels, plate: String, distanceCounted: Double) {
        vehicleViewModels.registerDistance(plate, distanceCounted.toInt())
    }
}
