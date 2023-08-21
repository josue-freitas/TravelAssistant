package pt.ipp.estg.myapplication.models.database.user

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class User(
    val email: String = "",
    val name: String = "",
    val birthDate: String = "",
    val registeredDate: String = getActualDate(),
    val contact: Int = 0,
) {
}

fun getActualDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return LocalDateTime.now().format(formatter)
}