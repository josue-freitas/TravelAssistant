package pt.ipp.estg.myapplication.models.retrofit.search_place

data class Place(
    val formatted_address: String,
    val geometry: Geometry,
    val name: String,
    val distance: Double
)
