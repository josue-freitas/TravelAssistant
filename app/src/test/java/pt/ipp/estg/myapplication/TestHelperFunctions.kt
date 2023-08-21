package pt.ipp.estg.myapplication

import com.google.android.gms.maps.model.LatLng
import org.junit.Test

import org.junit.Assert.*
import pt.ipp.estg.myapplication.helper.Location
import pt.ipp.estg.myapplication.helper.Validations

class TestHelperFunctions {

    @Test
    fun calculateDistance() {
        val distance =
            Location.calculateByDistance(LatLng(41.366942, -8.194774), LatLng(41.368543, -8.200705))
        assertEquals(0.53, distance, 0.0003)
    }

    @Test
    fun calculateDistanceSameLocation() {
        val distance =
            Location.calculateByDistance(LatLng(41.368543, -8.200705), LatLng(41.368543, -8.200705))
        assertEquals(0.0, distance, 0.0001)
    }

    @Test
    fun validatePlateCorrect() {
        assertEquals(true, Validations.validatePlate("00-AA-00"))
    }

    @Test
    fun validatePlateIncorrect() {
        assertEquals(false, Validations.validatePlate("AA-AA-AA"))
    }

    @Test
    fun validateNameCorrect() {
        assertEquals(true, Validations.validateName("User"))
    }

    @Test
    fun validateNameIncorrect() {
        assertEquals(false, Validations.validateName("Name12"))
    }

    @Test
    fun validatePasswordCorrect() {
        assertEquals(true, Validations.validatePassword("password"))
    }

    @Test
    fun validatePasswordIncorrect() {
        assertEquals(false, Validations.validateName("pass"))
    }

    @Test
    fun validateEmailCorrect() {
        assertEquals(true, Validations.validateEmail("teste@teste.com"))
    }

    @Test
    fun validateEmailIncorrect() {
        assertEquals(false, Validations.validateEmail("teste.teste.com"))
    }

    @Test
    fun validateContactCorrect() {
        assertEquals(true, Validations.validateContact(910910910))
    }

    @Test
    fun validateContactIncorrect() {
        assertEquals(false, Validations.validateContact(111000111))
    }

    @Test
    fun validateBirthDateCorrect() {
        assertEquals(true, Validations.validateBirthDate("12/12/2000"))
    }

    @Test
    fun validateBirthDateIncorrect() {
        assertEquals(false, Validations.validateBirthDate("01/11/2020"))
    }

}