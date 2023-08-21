package pt.ipp.estg.myapplication.models.database.gas_station

import androidx.room.Entity
import pt.ipp.estg.myapplication.enumerations.CouponType

@Entity(primaryKeys = ["Id", "Combustivel"])
data class GasStation(
    val Combustivel: String,
    val Distrito: String,
    val Id: Int,
    val Latitude: Double,
    val Localidade: String,
    val Longitude: Double,
    val Marca: String,
    val Morada: String,
    val Municipio: String,
    val Preco: Float,
    var PrecoComDesconto: Float,
    var TipoDesconto: CouponType?,
    var Distance: Double?
)
