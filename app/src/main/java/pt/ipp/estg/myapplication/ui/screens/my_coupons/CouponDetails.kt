package pt.ipp.estg.myapplication.ui.screens.my_coupons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.enumerations.GasType
import pt.ipp.estg.myapplication.models.database.coupon.Coupon
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.models.database.coupon.GasTypeList
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelLarge
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleSmall

/*
  Disclaimer: This preview its gonna be empty if there is no coupon with the id 1.
 */
@Preview
@Composable
fun PreviewCouponDetails() {
    val navController: NavHostController = rememberNavController()
    CouponDetails(
        1, navController
    )
}

@Composable
fun CouponDetails(id: Int, navController: NavController) {
    val couponViewModels: CouponViewModels = viewModel()
    val couponDetail = couponViewModels.getCouponById(id).observeAsState()

    CouponsTemplate {
        //get actual size of screen
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val openDialog = remember { mutableStateOf(false) }

        if (couponDetail.value != null) {
            val couponTypeString = if (couponDetail.value!!.type == CouponType.PERLITER) {
                stringResource(id = R.string.discount_per_litre)
            } else {
                stringResource(id = R.string.discount_per_total_value)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TitleSmall(
                        text = stringResource(id = R.string.coupon_information),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { openDialog.value = true }) {
                            Icon(
                                Icons.Filled.Delete, contentDescription = "Localized description",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.heightIn(10.dp))
                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 15.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            TitleSmall(
                                text = couponDetail.value!!.brandGasStation,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Image(
                                painter = painterResource(id = R.drawable.coupon),
                                contentDescription = "image",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                TitleSmall(
                    text = stringResource(id = R.string.basic_information),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 25.dp, horizontal = 15.dp)
                    ) {
                        //LEFT COLUMN
                        Column(
                            modifier = Modifier.width((screenWidth / 2).dp)
                        ) {
                            LabelMedium(
                                text = couponTypeString,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        //RIGHT COLUMN
                        Column(
                            modifier = Modifier.width((screenWidth / 2).dp)
                        ) {
                            LabelMedium(
                                text = couponDetail.value!!.discountVale.toString() + " €",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(35.dp))

                TitleSmall(
                    text = stringResource(id = R.string.detailed_information),
                    color = MaterialTheme.colorScheme.tertiary
                )

                Spacer(modifier = Modifier.heightIn(20.dp))

                DetailedInformation(coupon = couponDetail.value!!)

                Spacer(modifier = Modifier.heightIn(20.dp))

                if (openDialog.value) {

                    AlertDialog(
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            LabelLarge(
                                text = stringResource(id = R.string.delete_confirmation),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        text = {
                            LabelSmall(
                                text = stringResource(id = R.string.irreversible_action),
                                color = MaterialTheme.colorScheme.outline
                            )
                        },
                        confirmButton = {
                            OutlinedButton(
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                onClick = {
                                    openDialog.value = false
                                    couponViewModels.deleteCoupon(couponDetail.value!!.couponId)
                                    navController.navigate("my_coupons_screen")
                                }) {
                                Text(text = stringResource(id = R.string.delete_coupon))
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                onClick = { openDialog.value = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDetailedInformation() {
    DetailedInformation(
        Coupon(
            CouponType.PERLITER,
            "GALP",
            0.10f,
            10,
            30,
            GasTypeList(
                listOf(
                    GasType.LPG,
                    GasType.SIMPLEDIESEL,
                    GasType.SIMPLEGASOLINE98,
                    GasType.SIMPLEGASOLINE95
                )
            ),
            null,
            null
        )
    )
}

@Composable
fun DetailedInformation(coupon: Coupon) {
    //get actual size of screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    Card(
        backgroundColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(15.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp, horizontal = 15.dp)
        ) {
            //LEFT COLUMN
            Column(
                modifier = Modifier.width((screenWidth / 2).dp)
            ) {
                //MINIMAL LITERS
                LabelLarge(
                    text = stringResource(id = R.string.minimum_supply),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(5.dp))
                if (coupon.minimalLiters == null) {
                    LabelSmall(
                        text = stringResource(id = R.string.not_applicable),
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    LabelSmall(
                        text = coupon.minimalLiters.toString(),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LabelLarge(
                    text = stringResource(id = R.string.start_date),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (coupon.startDate == null) {
                    LabelSmall(
                        text = stringResource(id = R.string.not_applicable),
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    LabelSmall(
                        text = coupon.startDate.toString(),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LabelLarge(
                    text = stringResource(id = R.string.applied_gas_types),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                coupon.gasTypesApplied.gasTypeApplied.forEach {
                    LabelSmall(
                        text = it.englishName,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            //RIGHT COLUMN
            Column(
                modifier = Modifier
                    .width((screenWidth / 2).dp),
            ) {
                LabelLarge(
                    text = stringResource(id = R.string.max_supply),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(5.dp))
                if (coupon.maxLiters == null) {
                    LabelSmall(
                        text = stringResource(id = R.string.not_applicable),
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    LabelSmall(
                        text = coupon.minimalLiters.toString(),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LabelLarge(
                    text = stringResource(id = R.string.end_date),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (coupon.endDate == null) {
                    LabelSmall(
                        text = stringResource(id = R.string.not_applicable),
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    LabelSmall(
                        text = coupon.endDate.toString(),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
