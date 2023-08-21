package pt.ipp.estg.myapplication.models.database.coupon

import androidx.room.TypeConverter
import com.google.gson.Gson

class RoomTypeConverters {
    @TypeConverter
    fun convertInvoiceListToJSONString(gasTypeList: GasTypeList): String =
        Gson().toJson(gasTypeList)

    @TypeConverter
    fun convertJSONStringToInvoiceList(jsonString: String): GasTypeList =
        Gson().fromJson(jsonString, GasTypeList::class.java)
}