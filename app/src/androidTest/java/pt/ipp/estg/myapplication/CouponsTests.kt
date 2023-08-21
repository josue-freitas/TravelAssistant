package pt.ipp.estg.myapplication

import android.view.KeyEvent.*
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.ui.screens.my_coupons.CreateCoupon
import pt.ipp.estg.myapplication.ui.screens.vehicle.CreateVehicle

class CouponsTests {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun brandGasStationValidation_CouponsForm() {

        lateinit var brandGasError: String
        lateinit var myCoupons: String

        rule.setContent {
            CreateCoupon(
                navController = rememberNavController()
            )
            brandGasError = stringResource(id = R.string.brand_gas_input)
            myCoupons = stringResource(id = R.string.my_coupons)
        }

        //string that must appears
        rule.onNodeWithText(myCoupons).assertExists()

        //adds a bad wrong brand gas station brand
        rule.onNode(
            hasTestTag("brand_gas_input")
        ).performTextInput("a")
        //warning should appears
        rule.onNodeWithText(brandGasError).assertExists()

        //adds a bad wrong brand gas station brand
        rule.onNode(
            hasTestTag("brand_gas_input")
        ).performTextReplacement("2")
        //warning should appears
        rule.onNodeWithText(brandGasError).assertExists()

        //adds a bad wrong brand gas station brand
        rule.onNode(
            hasTestTag("brand_gas_input")
        ).performTextReplacement(".")
        //warning should appears
        rule.onNodeWithText(brandGasError).assertExists()

        //adds a correct brand
        rule.onNode(
            hasTestTag("brand_gas_input")
        ).performTextReplacement("CEPSA")
        //warning should NOT appears
        rule.onNodeWithText(brandGasError).assertDoesNotExist()
    }

    @Test
    fun allFormValidations_CouponsForm() {

        lateinit var brandGasError: String
        lateinit var myCoupons: String

        rule.setContent {
            CreateCoupon(
                navController = rememberNavController()
            )
            brandGasError = stringResource(id = R.string.brand_gas_input)
            myCoupons = stringResource(id = R.string.my_coupons)
        }

        //string that must appears
        rule.onNodeWithText(myCoupons).assertExists()

        //adds a correct brand gas station brand
        rule.onNode(hasTestTag("brand_gas_input")).performTextInput("CEPSA")
        //warning should appears
        rule.onNodeWithText(brandGasError).assertDoesNotExist()

        //adds a correct discount amount
        rule.onNode(hasTestTag("discount_input")).performClick().performKeyPress(
            KeyEvent(nativeKeyEvent = NativeKeyEvent(ACTION_DOWN, KEYCODE_3))
        )
        rule.onRoot()
            .performKeyPress(KeyEvent(nativeKeyEvent = NativeKeyEvent(ACTION_DOWN, KEYCODE_BACK)))
        rule.onNode(hasTestTag("root")).performScrollToNode(hasTestTag("submit_button"))

        //disable the min and max supply
        rule.onNode(hasTestTag("min_supply_check")).performClick()
        rule.onNode(hasTestTag("max_supply_check")).performClick()

        //disable all gas type
        rule.onNode(hasTestTag("diesel_check")).performClick()
        rule.onNode(hasTestTag("95_check")).performClick()
        rule.onNode(hasTestTag("lpg_check")).performClick()
        rule.onNode(hasTestTag("98_check")).performClick()

        //cant create, after submit must continue appearing the submit button
        rule.onNode(hasTestTag("submit_button")).performClick()
        rule.onNode(hasTestTag("submit_button")).assertExists()

        //disable start and end time
        rule.onNode(hasTestTag("start_check")).performClick()
        rule.onNode(hasTestTag("end_check")).performClick()

        //cant create, after submit must continue appearing the submit button
        rule.onNode(hasTestTag("submit_button")).performClick()
        rule.onNode(hasTestTag("submit_button")).assertExists()
    }
}