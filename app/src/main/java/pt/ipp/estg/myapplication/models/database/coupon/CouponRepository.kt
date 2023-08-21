package pt.ipp.estg.myapplication.models.database.coupon

import androidx.lifecycle.LiveData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CouponRepository(private val couponDao: CouponDao) {

    fun getCoupons(): LiveData<List<Coupon>> {
        return couponDao.getCoupons()
    }

    fun getValidCoupons(): LiveData<List<Coupon>> {
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val currentDate = LocalDateTime.now().format(formatter)
        return couponDao.getValidCoupons(currentDate)
    }

    suspend fun insert(coupon: Coupon) {
        return couponDao.insert(coupon)
    }

    suspend fun delete(id: Int) {
        couponDao.delete(id)
    }

    suspend fun deleteAll(){
        couponDao.deleteAll()
    }

    fun getCouponById(id: Int): LiveData<Coupon> {
        return couponDao.getCouponById(id)
    }

    fun getCouponsExpiringToday(currentDate: String): LiveData<List<Coupon>>{
        return couponDao.getCouponsExpiringToday(currentDate)
    }
}