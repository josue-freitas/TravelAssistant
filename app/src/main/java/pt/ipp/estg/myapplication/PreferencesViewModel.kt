package pt.ipp.estg.myapplication.ui.screens.locations

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.*
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.TypeLocation
import pt.ipp.estg.myapplication.models.firebase.StorageRepository
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.RetrofitHelperReverseGeoCoding
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.ReverseGeocodingAPI
import pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding.ReverseGeocodingResponse
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import pt.ipp.estg.myapplication.enumerations.SystemTheme

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {

    val storageRepository = StorageRepository()

    var preferencesHome: MutableLiveData<PreferencesData> = MutableLiveData()
    var preferencesWork: MutableLiveData<PreferencesData> = MutableLiveData()
    var preferencesPark: MutableLiveData<PreferencesData> = MutableLiveData()

    private var homePref: SharedPreferences =
        application.baseContext.getSharedPreferences("homePref", Context.MODE_PRIVATE)
    private var workPref: SharedPreferences =
        application.baseContext.getSharedPreferences("workPref", Context.MODE_PRIVATE)
    private var parkPref: SharedPreferences =
        application.baseContext.getSharedPreferences("parkPref", Context.MODE_PRIVATE)
    private val settingsPref: SharedPreferences =
        application.baseContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    var streetName: MutableLiveData<String> = MutableLiveData()
    var districtName: MutableLiveData<String> = MutableLiveData()
    var countryName: MutableLiveData<String> = MutableLiveData()
    var imgName: MutableLiveData<String> = MutableLiveData()

    var typeLocation = MutableLiveData<TypeLocation>()

    private val restAPIReverseGeocoding =
        RetrofitHelperReverseGeoCoding.getInstance().create(ReverseGeocodingAPI::class.java)

    var theme: MutableLiveData<SystemTheme> = MutableLiveData()
    var language = MutableLiveData("en")
    var plateTracking = MutableLiveData(" ")
    var isToShowWelcomeScreen = MutableLiveData(true)
    var subFuelNotification = MutableLiveData(true)
    var subFuelIsLoading = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updatePreferencesOnInit(homePref, preferencesHome)
            updatePreferencesOnInit(workPref, preferencesWork)
            updatePreferencesOnInit(parkPref, preferencesPark)
            updateSettingPreferencesOnInit(theme, language, plateTracking)
        }
    }

    private fun updateSettingPreferencesOnInit(
        theme: MutableLiveData<SystemTheme>,
        language: MutableLiveData<String>,
        plate: MutableLiveData<String>
    ) {
        val themePref = settingsPref.getString("theme", SystemTheme.SYSTEM.toString())
        val langPref = settingsPref.getString("lang", "en")
        val platePref = settingsPref.getString("plateTrack", " ")
        val isToShowWelcomeScreenPref = settingsPref.getBoolean("isToShowWelcomeScreen", true)
        val subFuelNotificationPref = settingsPref.getBoolean("subFuelNotification", false)
        theme.postValue(
            enumValueOf(
                themePref.toString()
            )
        )
        language.postValue(langPref)
        isToShowWelcomeScreen.postValue(isToShowWelcomeScreenPref)
        plateTracking.postValue(platePref)
        subFuelNotification.postValue(subFuelNotificationPref)
    }

    fun updateWelcomeScreen(context: Context, newState: Boolean) {
        val preferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        isToShowWelcomeScreen.value = newState
        val editor = preferences.edit()
        editor.apply {
            editor.putBoolean("isToShowWelcomeScreen", newState)
            apply()
        }
    }

    fun updateSubFuelNotification(context: Context, newState: Boolean) {
        val preferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        subFuelNotification.value = newState
        val editor = preferences.edit()
        editor.apply {
            editor.putBoolean("subFuelNotification", newState)
            apply()
        }
    }

    private fun updatePreferencesOnInit(
        sharedPreferences: SharedPreferences,
        mutableLiveData: MutableLiveData<PreferencesData>
    ) {
        val streetPrefHome = sharedPreferences.getString("street", " ")
        val districtPrefHome = sharedPreferences.getString("district", " ")
        val countryPrefHome = sharedPreferences.getString("country", " ")
        val lastUpdateHome = sharedPreferences.getString("lastUpdate", " ")
        val latHome = sharedPreferences.getString("lat", 0.0.toString())
        val longHome = sharedPreferences.getString("long", 0.0.toString())
        val imgName = sharedPreferences.getString("imgName", " ")

        mutableLiveData.postValue(
            PreferencesData(
                streetPrefHome,
                districtPrefHome,
                countryPrefHome,
                lastUpdateHome,
                latHome,
                longHome,
                imgName
            )
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updatePreference(location: LatLng, locationType: TypeLocation? = typeLocation.value) {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Default) {
                getReverseGeoCodingInformation(
                    location
                )
            }

            withContext(Dispatchers.Main) {
                val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                val currentDate = LocalDateTime.now().format(formatter)

                val preferencesData: PreferencesData
                if (location.longitude == 0.0 && location.latitude == 0.0) {
                    preferencesData = PreferencesData(
                        " ",
                        " ",
                        " ",
                        currentDate,
                        location.latitude.toString(),
                        location.longitude.toString(),
                        imgName.value
                    )
                } else {
                    preferencesData = PreferencesData(
                        streetName.value,
                        districtName.value,
                        countryName.value,
                        currentDate,
                        location.latitude.toString(),
                        location.longitude.toString(),
                        imgName.value
                    )
                }

                when (locationType) {
                    TypeLocation.HOME -> {
                        updatePreferences(homePref, preferencesHome, preferencesData)
                    }
                    TypeLocation.WORK -> {
                        updatePreferences(workPref, preferencesWork, preferencesData)
                    }
                    TypeLocation.VEHICLE -> {
                        updatePreferences(parkPref, preferencesPark, preferencesData)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updatePreferences(
        sharedPreferences: SharedPreferences,
        mutableLiveData: MutableLiveData<PreferencesData>,
        preferencesData: PreferencesData
    ) {
        val editor = sharedPreferences.edit()

        editor.apply {
            putString("street", preferencesData.street)
            putString("district", preferencesData.district)
            putString("country", preferencesData.country)
            putString("lastUpdate", preferencesData.lastUpdate)
            putString("lat", preferencesData.lat)
            putString("long", preferencesData.long)
            putString("imgName", preferencesData.imgName)
            apply()
        }

        mutableLiveData.postValue(preferencesData)
    }

    fun updateTheme(newTheme: SystemTheme) {
        viewModelScope.launch {
            theme.value = newTheme
            val editor = settingsPref.edit()
            editor.apply {
                editor.putString("theme", newTheme.toString())
                apply()
            }
            Log.d("theme", "viewmodel ${theme.value}")
        }
    }

    fun updateLanguage(context: Context, newLanguage: String) {
        Log.e("UPDATE", newLanguage)
        viewModelScope.launch {
            val appLocale: LocaleListCompat =
                LocaleListCompat.forLanguageTags(newLanguage)
            AppCompatDelegate.setApplicationLocales(appLocale)

            val editor = settingsPref.edit()
            editor.apply {
                editor.putString("lang", newLanguage)
                apply()
            }
            language.value = newLanguage
            Log.d("lang", "viewmodel ${language.value}")
        }
    }

    fun insertPlateRecord(plate: String) {
        viewModelScope.launch {
            plateTracking.postValue(plate)
            val editor = settingsPref.edit()
            editor.apply {
                editor.putString("plateTrack", plate)
                apply()
            }
        }
    }

    fun setTypeLocation(type: TypeLocation) {
        typeLocation.value = type
    }

    private suspend fun getReverseGeoCodingInformation(location: LatLng) {
        try {
            val response = getDistrict(location)
            if (response.isSuccessful) {
                val locationDetails =
                    response.body()?.results?.get(0)?.address_components?.get(2)
                val locationDetails2 =
                    response.body()?.results?.get(0)?.address_components?.get(3)
                val streetDetails = response.body()?.results?.get(0)?.address_components?.get(0)
                val streetDetails2 = response.body()?.results?.get(0)?.address_components?.get(1)
                val country = response.body()?.results?.get(0)?.address_components?.get(4)
                if (streetDetails?.types?.get(0) == "route") {
                    streetName.postValue(streetDetails.short_name)
                } else {
                    streetName.postValue(streetDetails2!!.short_name)
                }
                if (locationDetails2?.types?.get(0) == "administrative_area_level_1") {
                    districtName.postValue(locationDetails2.long_name)
                    countryName.postValue(country!!.long_name)
                } else {
                    districtName.postValue(locationDetails!!.long_name)
                    countryName.postValue(locationDetails2!!.long_name)
                }
            }
        } catch (ce: CancellationException) {
            throw ce // Needed for coroutine scope cancellation
        } catch (e: Exception) {
            //TODO toast!
        }
    }

    suspend fun getDistrict(location: LatLng): Response<ReverseGeocodingResponse> {
        return restAPIReverseGeocoding.getReverseGeocoding(
            "${location.latitude},${location.longitude}",
            "AIzaSyDko83WUIktst92Sp3z7YOVJDUWUOLVVY0"
        )
    }

    fun uploadLocationsToCloudByUser(
        context: Context, email: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            storageRepository.uploadLocationsToUser(
                email = email,
                homeLocations =
                LatLng(
                    preferencesHome.value!!.lat!!.toDouble(),
                    preferencesHome.value!!.long!!.toDouble()
                ),
                workLocations =
                LatLng(
                    preferencesWork.value!!.lat!!.toDouble(),
                    preferencesWork.value!!.long!!.toDouble()
                )
            ) { result ->
                if (result) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.upload_locations_success),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                } else {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.upload_locations_error),
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR,
                        false
                    ).show()
                }
            }
        }
    }

    fun removeLocationsCloudByUser(
        context: Context, email: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            storageRepository.uploadLocationsToUser(
                email = email,
                homeLocations =
                LatLng(
                    0.0.toDouble(),
                    0.0.toDouble()
                ),
                workLocations =
                LatLng(
                    0.0.toDouble(),
                    0.0.toDouble()
                )
            ) { result ->
                if (result) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.remove_location_sucess),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                } else {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.remove_location_error),
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR,
                        false
                    ).show()
                }
            }
        }
    }

    fun getCloudLocations(
        email: String,
        context: Context,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var isHomeEmpty = false
            var isWorkEmpty = false
            storageRepository.getLocations(email, onError = {
                FancyToast.makeText(
                    context,
                    context.resources.getString(R.string.get_location_error),
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }, onSuccess = fun(home, work) {
                if (home != null) {
                    Log.d(
                        "lat",
                        "${home.latitude.toString()}  ${0.0.toString()} ${home.latitude.toString() != 0.0.toString()} "
                    )
                    Log.d(
                        "lat",
                        "${home.latitude.toString()}  ${0.0.toString()} ${home.latitude.toString() != 0.0.toString()} "
                    )
                    if (home.latitude.toString() != 0.0.toString() && home.longitude.toString() != 0.0.toString()) {
                        updatePreference(home, TypeLocation.HOME)
                    } else {
                        isHomeEmpty = true;
                    }
                }

                if (work != null) {
                    if (work.latitude.toString() != 0.0.toString() && work.longitude.toString() != 0.0.toString()) {
                        updatePreference(work, TypeLocation.WORK)
                    } else {
                        isWorkEmpty = true;
                    }
                }

                if (isHomeEmpty && isWorkEmpty) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_locations_empty),
                        FancyToast.LENGTH_LONG,
                        FancyToast.WARNING,
                        false
                    ).show()
                } else if (isHomeEmpty) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_home_empty),
                        FancyToast.LENGTH_LONG,
                        FancyToast.WARNING,
                        false
                    ).show()
                } else if (isWorkEmpty) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_work_empty),
                        FancyToast.LENGTH_LONG,
                        FancyToast.WARNING,
                        false
                    ).show()
                } else {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_location_sucess),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
            }
            )
        }
    }

    fun subscribeToTopic(context: Context, topic: String) {
        subFuelIsLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            Firebase.messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    subFuelIsLoading.postValue(false)
                    if (!task.isSuccessful) {
                        Log.d("FireBaseMessaging", "error subscribed to fuel notification")
                        FancyToast.makeText(
                            context,
                            context.getString(R.string.sub_error),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    } else {
                        updateSubFuelNotification(context, true)
                        subFuelIsLoading.postValue(false)
                        Log.d("FireBaseMessaging", "subscribed to fuel notification")
                        FancyToast.makeText(
                            context,
                            context.getString(R.string.subcribed),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    }
                }
        }
    }

    fun unsubscribeFromTopic(context: Context, topic: String) {
        subFuelIsLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            Firebase.messaging.unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    subFuelIsLoading.postValue(false)
                    if (!task.isSuccessful) {
                        FancyToast.makeText(
                            context,
                            context.getString(R.string.sub_cancelled_error),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    } else {
                        updateSubFuelNotification(context, false)
                        subFuelIsLoading.postValue(false)
                        FancyToast.makeText(
                            context,
                            context.getString(R.string.sub_cancelled),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    }
                }
            Log.d("FireBaseMessaging", "deleted token")
        }
    }
}