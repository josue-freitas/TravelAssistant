package pt.ipp.estg.myapplication.models.database.charge_station

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.retrofit.charge_station.*

class ChargeStationViewModels(application: Application) : AndroidViewModel(application) {
    private val repository: ChargeStationRepository
    var chargesStations: MutableLiveData<MutableList<ChargeStationResponseItem>> = MutableLiveData()

    init {
        val restAPIChargerStations = RetrofitHelpChargerStations.getInstance().create(
            ChargerStationAPI::class.java
        )
        repository = ChargeStationRepository(restAPIChargerStations)
    }

    fun getChargeStations(currentLocation: LocationDetails?, distance: Int) {
        Log.e("LOCATION", currentLocation.toString())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getChargerStations(currentLocation, distance)
                if (response.isSuccessful) {
                    val mutableList = mutableListOf<ChargeStationResponseItem>()
                    response.body()?.forEach {
                        mutableList.add(it)
                    }
                    chargesStations.postValue(mutableList)
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
}