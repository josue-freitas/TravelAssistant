package pt.ipp.estg.myapplication.helper

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalField
import java.util.*

class Validations {

    companion object {
        fun validatePlate(plate: String): Boolean {
            val regex =
                "(^[A-Z]{2}-\\d\\d-[A-Z]{2}\$)|(^\\d\\d-[A-Z]{2}-\\d\\d\$)|(^\\d\\d-\\d\\d-[A-Z]{2}\$)".toRegex()
            val result = regex.find(plate)
            return result != null
        }

        fun validateName(name: String): Boolean {
            val regex =
                "(^[A-Z][a-z]{2,}(\\s[A-Z][a-z]+)*$)".toRegex()
            val result = regex.find(name)
            return result != null
        }

        fun validatePassword(password: String): Boolean {
            val regex =
                "(^[a-zA-Z!#?0-9%^]{6,}\$)".toRegex()
            val result = regex.find(password)
            return result != null
        }

        fun validateEmail(email: String): Boolean {
            val regex =
                "(^[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]+\$)".toRegex()
            val result = regex.find(email)
            return result != null
        }

        fun validateContact(contact: Int): Boolean {
            return contact in 900000001..999999998
        }

        @SuppressLint("SimpleDateFormat")
        fun validateBirthDate(birthDate: String): Boolean {
            if (birthDate == "") return true
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val beforeCurrentDate = LocalDateTime.now().toLocalDate().minusYears(12).format(formatter)


            val formatter2 = SimpleDateFormat("dd/MM/yyyy")
            formatter2.timeZone = TimeZone.getTimeZone("Europe/Lisbon");

            val dateReceived = formatter2.parse(birthDate)
            val currentDate = formatter2.parse(beforeCurrentDate)

            if (dateReceived != null) {
                return dateReceived < currentDate
            }
            return false
        }

    }
}