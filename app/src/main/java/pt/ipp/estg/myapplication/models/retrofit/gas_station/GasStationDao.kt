package pt.ipp.estg.myapplication.models.retrofit.gas_station

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.models.database.gas_station.GasStation

@Dao
interface GasStationDao {

    @Query("select * from GasStation where Combustivel = :combustivel and Distance < :rangeDistance order by Distance")
    fun getGasStationsByDistance(combustivel: String, rangeDistance: Int): MutableList<GasStation>

    @Query("select * from GasStation where Combustivel = :combustivel and Distance < :rangeDistance order by PrecoComDesconto")
    fun getGasStationsByPrice(combustivel: String, rangeDistance: Int): MutableList<GasStation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasStation(gasStation: GasStation)

    @Query("select distrito from GasStation")
    fun getDistrictInRoom(): String

    @Query("delete from GasStation")
    fun deleteGasStations()

    @Query("update GasStation set Distance = :distance, PrecoComDesconto = :precoComDesconto, TipoDesconto = :tipoDesconto where Id = :id and Combustivel = :combustivel")
    fun updateDistanceAndCoupons(
        id: Int,
        combustivel: String,
        distance: Double,
        precoComDesconto: Float,
        tipoDesconto: CouponType
    )
}