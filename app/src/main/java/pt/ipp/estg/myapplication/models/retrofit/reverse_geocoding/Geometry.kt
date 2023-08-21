package pt.ipp.estg.myapplication.models.retrofit.reverse_geocoding

data class Geometry(
    val bounds: Bounds,
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)