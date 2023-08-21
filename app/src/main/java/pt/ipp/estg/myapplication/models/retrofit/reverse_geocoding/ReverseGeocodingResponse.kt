package pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding

data class ReverseGeocodingResponse(
    val plus_code: PlusCode,
    val results: List<Result>,
    val status: String
)