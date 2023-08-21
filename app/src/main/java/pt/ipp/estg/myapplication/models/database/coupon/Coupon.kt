package pt.ipp.estg.myapplication.models.database.coupon

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ipp.estg.myapplication.enumerations.CouponType

@Entity
data class Coupon(
    val type: CouponType,
    val brandGasStation: String,
    val discountVale: Float,
    val minimalLiters: Int?,
    val maxLiters: Int?,
    val gasTypesApplied: GasTypeList,
    val startDate: String?,
    val endDate: String?,
    var valid: Boolean? = null
){
    @PrimaryKey(autoGenerate = true)
    var couponId: Int = 0
}