package pt.ipp.estg.myapplication.models.retrofit.search_place

data class Results(
    val formatted_address: String,
    val geometry: Geometry,
    val name: String,
    val types: List<String>,
)