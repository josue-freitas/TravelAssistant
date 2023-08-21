package pt.ipp.estg.myapplication.ui.screens.my_coupons

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.enumerations.GasType
import pt.ipp.estg.myapplication.models.database.coupon.Coupon
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.models.database.coupon.GasTypeList

@Preview
@Composable
fun PreviewCreateCoupon() {
    val navController: NavHostController = rememberNavController()
    CreateCoupon (navController)
}

@Composable
fun CreateCoupon(
    navController: NavController
) {
    val couponsViewModel: CouponViewModels = viewModel()

    val couponType = rememberSaveable { mutableStateOf(CouponType.PERLITER) }

    val brandGasStation = rememberSaveable { mutableStateOf("") }
    val discountValue = rememberSaveable { mutableStateOf(0f) }

    val minSupplyValue = rememberSaveable { mutableStateOf(0) }
    val minSupplyCheck = rememberSaveable { mutableStateOf(true) }

    val maxSupplyValue = rememberSaveable { mutableStateOf((0)) }
    val maxSupplyCheck = rememberSaveable { mutableStateOf(true) }

    val dieselCheck = rememberSaveable { mutableStateOf(true) }
    val glpCheck = rememberSaveable { mutableStateOf(true) }
    val gasoline95Check = rememberSaveable { mutableStateOf(true) }
    val gasoline98Check = rememberSaveable { mutableStateOf(true) }

    val startDateCheck = rememberSaveable { mutableStateOf(true) }
    val endDateCheck = rememberSaveable { mutableStateOf(true) }
    val startDate = rememberSaveable { mutableStateOf("") }
    val endDate = rememberSaveable { mutableStateOf("") }

    fun addCoupon() {
        val gasTypesApplied = mutableListOf<GasType>()

        if (dieselCheck.value) gasTypesApplied.add(GasType.SIMPLEDIESEL)
        if (glpCheck.value) gasTypesApplied.add(GasType.LPG)
        if (gasoline95Check.value) gasTypesApplied.add(GasType.SIMPLEGASOLINE95)
        if (gasoline98Check.value) gasTypesApplied.add(GasType.SIMPLEGASOLINE98)

        val listGasTypesApplied: List<GasType> = gasTypesApplied.toList()

        GasTypeList(listGasTypesApplied)

        val coupon = Coupon(
            type = couponType.value,
            brandGasStation = brandGasStation.value,
            discountVale = discountValue.value,

            minimalLiters = if (minSupplyCheck.value) minSupplyValue.value else null,
            maxLiters = if (maxSupplyCheck.value) maxSupplyValue.value else null,

            gasTypesApplied = GasTypeList(listGasTypesApplied),

            startDate = if (startDateCheck.value) startDate.value else null,
            endDate = if (endDateCheck.value) endDate.value else null
        )

        Log.d("INSERTED COUPON", coupon.toString())

        couponsViewModel.insert(coupon)
        navController.navigate("my_coupons_screen")
    }

    CouponsTemplate {
        Column(
            modifier = Modifier
                .testTag("root")
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            CouponInfoInsertion(
                couponType = couponType,
                brandGasStation = brandGasStation,
                discountValue = discountValue,
                minSupplyValue = minSupplyValue,
                minSupplyCheck = minSupplyCheck,
                maxSupplyValue = maxSupplyValue,
                maxSupplyCheck = maxSupplyCheck,
                dieselCheck = dieselCheck,
                glpCheck = glpCheck,
                gasoline95Check = gasoline95Check,
                gasoline98Check = gasoline98Check,
                startDate = startDate,
                startDateCheck = startDateCheck,
                endDate = endDate,
                endDateCheck = endDateCheck,
                addCoupon = { addCoupon() },
                navController = navController
            )
        }
    }
}