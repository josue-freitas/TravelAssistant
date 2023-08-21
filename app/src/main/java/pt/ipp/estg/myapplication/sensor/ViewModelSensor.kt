package pt.ipp.estg.myapplication.sensor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ViewModelSensor(application: Application) : AndroidViewModel(application) {
    lateinit var lightSensor: MeasurableSensor
    val isDark = MutableLiveData(false)

    fun onInit(lightSensor: MeasurableSensor) {
        this.lightSensor = lightSensor
        lightSensor.startListening()
        lightSensor.setOnSensorValuesChangedListener { values ->
            val lux = values[0]
            isDark.postValue(lux < 100f)
        }
    }
}