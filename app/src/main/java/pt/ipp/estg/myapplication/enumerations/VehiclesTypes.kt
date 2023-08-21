package pt.ipp.estg.myapplication.enumerations

enum class VehiclesTypes {
    AUTOMOBILE, MOTORCYCLE
}

enum class AssociationVehicles {
    PERSONAL, PROFISSIONAL
}

enum class MaintenanceStatus {
    WITHOUT_DATA, DANGER, WARNING, OK
}

//km for warning
val MIN_KM_SHIMS = 2000
val MEDIUM_KM_SHIMS = 5000

val MIN_KM_DISKS = 5000
val MEDIUM_KM_DISKS = 8000

val MIN_KM_OIL = 2000
val MEDIUM_KM_OIL = 8000

val MIN_KM_CABIN = 2000
val MEDIUM_KM_CABIN = 8000