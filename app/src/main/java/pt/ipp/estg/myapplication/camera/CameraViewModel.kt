package pt.ipp.estg.myapplication.camera

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.io.File

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private var context = application.applicationContext

    var imgNameVehicle: String
    private var counterVehicle: Int

    var isLocationImgSaved = MutableLiveData(false)


    init {
        val cameraPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        //if there is no shared preferences yet
        if (!cameraPref.contains("imgName")) {
            val editor = cameraPref.edit()
            editor.apply {
                editor.putInt("imgCounter", 0)
                editor.putString("imgName", "img-0")
                apply()
            }
        }
        if (!cameraPref.contains("isLocationImgSaved")) {
            val editor = cameraPref.edit()
            editor.apply {
                editor.putBoolean("isLocationImgSaved", false)
                apply()
            }
        }
        isLocationImgSaved.value = cameraPref.getBoolean("isLocationImgSaved", false)
        imgNameVehicle = cameraPref.getString("imgName", null).toString()
        counterVehicle = cameraPref.getInt("imgCounter", 0)
    }

    fun updateImgName() {
        val cameraPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = cameraPref.edit()
        counterVehicle++
        editor.apply {
            editor.putString("imgName", "img-$counterVehicle")
            editor.putInt("imgCounter", counterVehicle)
            apply()
        }
    }

    fun deleteLocationImg() {
        val cameraPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = cameraPref.edit()
        editor.apply {
            editor.putBoolean("isLocationImgSaved", false)
            apply()
        }
        isLocationImgSaved.postValue(false)
    }

    fun saveLocationImg() {
        val cameraPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = cameraPref.edit()
        editor.apply {
            editor.putBoolean("isLocationImgSaved", true)
            apply()
        }
        isLocationImgSaved.postValue(true)
    }

    fun getOutputDirectory(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            //File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
            File(it, "imgs").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }
}