package pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)