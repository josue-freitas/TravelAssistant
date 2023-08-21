package pt.ipp.estg.myapplication.helper

import android.content.Context
import pt.ipp.estg.myapplication.R

class VehicleListInfo {

    companion object {

        //CAR

        fun getBrandListCar(context: Context): List<String> {
            val other = context.resources.getString(R.string.others)

            return listOf("Dacia", "BMW", "Volvo", "Mercedes", "Peugeot", other)
        }

        fun getModelListCar(brand: String, context: Context): List<String> {
            val other = context.resources.getString(R.string.others)
            if (brand == "Dacia") {
                return listOf("Sandero", "Lodgy", "Logan", "Duster", other)
            } else if (brand == "BMW") {
                return listOf(
                    "114",
                    "116",
                    "118",
                    "M135i",
                    "218",
                    "220",
                    "225i",
                    "M235i",
                    "M240i",
                    "315",
                    "316",
                    "320",
                    "330",
                    "M3",
                    "418",
                    "420",
                    "425",
                    "430",
                    "M4",
                    other
                )
            } else if (brand == "Volvo") {
                return listOf("V40", "V60", "V90", "S60", "S90", "XC40", "XC60", "XC90", other)
            } else if (brand == "Mercedes") {
                return listOf(
                    "Class A",
                    "Class B",
                    "Class C",
                    "Class E",
                    "Class S",
                    "GLA",
                    "GLC",
                    "GLE",
                    "GT63",
                    other
                )
            } else if (brand == "Peugeot") {
                return listOf("208", "308", "408", "508", "5008", "Boxer", "Expert", other)
            } else if (brand == other) {
                return listOf(other)
            } else {
                return listOf()
            }
        }

        fun getSpecificationCar(context: Context, model: String): List<String> {
            val other = context.resources.getString(R.string.others)

            if (model == "") {
                return listOf()
            } else {
                return listOf(
                    "1.0 75HP",
                    "1.0 90HP",
                    "1.0 100HP",
                    "1.2 80HP",
                    "1.4 100HP",
                    "1.6 109HP",
                    "1.6 120HP",
                    "1.6 115HP",
                    "1.6 121HP",
                    "2.0 100HP",
                    "2.0 120HP",
                    "2.0 130HP",
                    "2.0 140HP",
                    "2.0 211HP",
                    "2.5 375HP",
                    "3.0 325HP",
                    "3.0 375HP",
                    "4.7 408HP",
                    other
                )
            }
        }


        //MOTORCYCLE

        fun getBrandListMotorcycle(context: Context): List<String> {
            val other = context.resources.getString(R.string.others)

            return listOf("Honda", "Yamaha", "Kawasaki", "BMW", other)
        }

        fun getModelListMotorcycle(brand: String, context: Context): List<String> {
            val other = context.resources.getString(R.string.others)
            if (brand == "Honda") {
                return listOf(
                    "CBR 1000",
                    "CBR 600",
                    "CBR 1000",
                    "REBEL 1OO",
                    "REBEL 300",
                    "REBEL 500",
                    "REBEL 1100",
                    other
                )
            } else if (brand == "BMW") {
                return listOf(
                    "S 1000R",
                    "S 1250RS",
                    "M 1000RR",
                    "F 900R",
                    other
                )
            } else if (brand == "Yamaha") {
                return listOf("YZF-R1M", "YZF-R3", "MT-03", "MT-10", "TRACER 9", "ZUMA 125", other)
            } else if (brand == "Kawasaki") {
                return listOf(
                    "NINJA H2",
                    "NINJA ZX-14",
                    "NINJA ZX-10",
                    "Z125",
                    "Z650",
                    "Z900",
                    "W800",
                    "NINJA 400",
                    "Z H2",
                    other
                )
            } else if (brand == other) {
                return listOf(other)
            } else {
                return listOf()
            }
        }

        fun getSpecificationMotorcycle(context: Context, model: String): List<String> {
            val other = context.resources.getString(R.string.others)

            if (model == "") {
                return listOf()
            } else {
                return listOf(
                    "50cc",
                    "125cc",
                    "250cc",
                    "300cc",
                    "400cc",
                    "500cc",
                    "600cc",
                    "750cc",
                    "1000cc",
                    "1000+ cc",
                    other
                )
            }
        }
    }
}