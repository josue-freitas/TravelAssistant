package pt.ipp.estg.myapplication.models.retrofit.charge_station

data class AddressInfo(
    val AddressLine1: String,
    val Country: Country,
    val Distance: Double,
    val Latitude: Double,
    val Longitude: Double,
    val RelatedURL: String,
    val StateOrProvince: String,
    val Town: String
)