package pt.ipp.estg.myapplication.helper

import com.google.android.gms.maps.model.LatLng
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Location {
    companion object {
        fun calculateByDistance(StartP: LatLng, EndP: LatLng): Double {
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
    }
}