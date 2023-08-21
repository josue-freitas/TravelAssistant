package pt.ipp.estg.myapplication.models.database.search_place

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.retrofit.search_place.*
import java.math.RoundingMode
import java.text.DecimalFormat

class SearchPlaceViewModels(application: Application) : AndroidViewModel(application) {
    private val repository: SearchPlaceRepository
    var places: MutableLiveData<MutableList<Place>> = MutableLiveData()

    init {
        val restSearchPlaceAPI =
            RetrofitHelperSearchPlace.getInstance().create(SearchPlaceAPI::class.java)
        repository = SearchPlaceRepository(restSearchPlaceAPI)
    }

    fun getSearchPlaces(place: String, location: LocationDetails?) {
        if (place == "") {
            places.value = null
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = repository.getSearch(place)
                    if (response.isSuccessful) {
                        val mutableList = mutableListOf<Place>()

                        response.body()?.results?.forEach {
                            mutableList.add(
                                Place(
                                    it.formatted_address,
                                    it.geometry,
                                    it.name,
                                    CalculationByDistance(
                                        LatLng(
                                            location!!.latitude,
                                            location!!.longitude
                                        ),
                                        LatLng(it.geometry.location.lat, it.geometry.location.lng)
                                    )
                                )
                            )
                        }
                        places.postValue(mutableList)
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

    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        val kms = df.format(Radius * c).toDouble()
        return kms
    }
}