package pt.ipp.estg.myapplication.models.database.coupon

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CouponDao {
    @Query("select * from Coupon")
    fun getCoupons(): LiveData<List<Coupon>>

    @Query("select * from Coupon where (((startDate IS NOT NULL and startDate <= :currentDate) or ( startDate IS NULL)) AND ((endDate IS NOT NULL and endDate >=  :currentDate) or ( endDate IS NULL)))")
    fun getValidCoupons(currentDate: String): LiveData<List<Coupon>>

    @Query("select * from Coupon where endDate = :currentDate")
    fun getCouponsExpiringToday(currentDate: String): LiveData<List<Coupon>>

    @Query("select * from Coupon where couponId = :id")
    fun getCouponById(id: Int): LiveData<Coupon>

    @Query("delete from Coupon where couponId = :id")
    suspend fun delete(id: Int)


    @Query("delete from Coupon")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coupon: Coupon)
}