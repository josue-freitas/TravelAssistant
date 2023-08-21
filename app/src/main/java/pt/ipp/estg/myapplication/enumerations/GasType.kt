package pt.ipp.estg.myapplication.enumerations

fun getGasTypeByName(gasName: String): GasType {
    var gasType = GasType.LPG
    when (gasName) {
        "GPL Auto" -> gasType = GasType.LPG
        "Gasolina simples 95" -> gasType = GasType.SIMPLEGASOLINE95
        "Gasolina 98" -> gasType = GasType.SIMPLEGASOLINE98
        "Gasóleo simples" -> gasType = GasType.SIMPLEDIESEL
    }
    return gasType
}

enum class GasType(val id: Int, val nameOnAPI: String, val englishName: String) {
    LPG(1120, "GPL Auto", "LPG"),
    SIMPLEGASOLINE95(3201, "Gasolina simples 95", "Gasoline 95"),
    SIMPLEGASOLINE98(3400, "Gasolina 98", "Gasoline 98"),
    SIMPLEDIESEL(2101, "Gasóleo simples", "Diesel"),
}