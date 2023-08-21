package pt.ipp.estg.myapplication.models.database.coupon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(value = [RoomTypeConverters::class])
@Database(entities = [Coupon::class], version = 9)
abstract class CouponDatabase : RoomDatabase() {

    abstract fun getCouponDao(): CouponDao

    companion object {
        private var INSTANCE: CouponDatabase? = null

        fun getDatabase(context: Context): CouponDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    CouponDatabase::class.java,
                    "coupon-database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}