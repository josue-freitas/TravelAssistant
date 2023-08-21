package pt.ipp.estg.myapplication.models.database.vehicle

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Vehicle::class], version = 6)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun getVehicleDao(): VehicleDao

    companion object {
        private var INSTANCE: VehicleDatabase? = null

        fun getDatabase(context: Context): VehicleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    VehicleDatabase::class.java,
                    "vehicle-database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
