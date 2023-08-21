package pt.ipp.estg.myapplication.models.database.gas_station

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipp.estg.myapplication.models.retrofit.gas_station.GasStationDao

@Database(entities = [GasStation::class], version = 23)
abstract class GasStationDatabase : RoomDatabase() {

    abstract fun getGasStationDao(): GasStationDao

    /*
        TODO: remove allowMainThreadQueries
     */
    companion object {
        private var INSTANCE: GasStationDatabase? = null

        fun getDatabase(context: Context): GasStationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    GasStationDatabase::class.java,
                    "gas-station"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}