package pt.ipp.estg.myapplication

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import pt.ipp.estg.myapplication.ui.screens.vehicle.CreateVehicle


class VehiclesTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun plateValidation_VehicleForm() {

        lateinit var plateBadString: String
        lateinit var plate: String
        lateinit var manualRegistration: String

        rule.setContent {
            CreateVehicle(
                navController = rememberNavController()
            )
            plateBadString = stringResource(id = R.string.bad_plate_input)
            plate = stringResource(id = R.string.plate)
            manualRegistration = stringResource(id = R.string.manual_registration)
        }

        //check if basic text appears
        rule.onNodeWithText(plate).assertExists()
        rule.onNodeWithText(manualRegistration).assertExists()

        //adds a bad plate
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextInput("12-AA-2")
        //text notifying the bad format appears
        rule.onNodeWithText(plateBadString).assertExists()

        //adds a bad plate
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextClearance()
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextInput("321dsadas")
        //text notifying the bad format appears
        rule.onNodeWithText(plateBadString).assertExists()

        //adds a bad plate
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextClearance()
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextInput("aa-12-12")
        //text notifying the bad format appears
        rule.onNodeWithText(plateBadString).assertExists()

        //adds a VALID plate
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextClearance()
        rule.onNode(
            hasTestTag("plate_input")
        ).performTextInput("12-AA-12")
        //text notifying the bad plate must not appears
        rule.onNodeWithText(plateBadString).assertDoesNotExist()
    }
}