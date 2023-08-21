package pt.ipp.estg.myapplication.ui.screens.my_coupons

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import pt.ipp.estg.myapplication.ui.screens.ui_components.SearchBar
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleSmall
import pt.ipp.estg.myapplication.ui.screens.vehicle.ViewType

/*
  Disclaimer: This preview its gonna be a little bit empty if there is no coupons
 */
@Preview
@Composable
fun PreviewMyCoupons() {
    val navController: NavHostController = rememberNavController()
    MyCoupons(navController)
}

@Composable
fun MyCoupons(
    navController: NavController
) {
    val searchBarInput = remember { mutableStateOf("") }

    val couponsViewModel: CouponViewModels = viewModel()
    val coupons = couponsViewModel.allCoupons.observeAsState()
    couponsViewModel.updateValidCoupons()
    val viewType = remember { mutableStateOf(ViewType.GRID) }

    CouponsTemplate {
        SearchBar(searchBarInput, stringResource(id = R.string.brand_gas_station))
        Spacer(modifier = Modifier.height(20.dp))

        if (coupons.value != null && coupons.value!!.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { viewType.value = ViewType.GRID }) {
                        Icon(
                            imageVector = Icons.Default.GridView, contentDescription = "",
                            tint = if (viewType.value == ViewType.GRID) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { viewType.value = ViewType.LIST }) {
                        Icon(
                            imageVector = Icons.Default.ViewList, contentDescription = "",
                            tint = if (viewType.value == ViewType.LIST) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)
        ListOfCoupons(navController, coupons, searchBarInput, viewType)
    }
}

/*
  Disclaimer: This preview its gonna be a little bit empty if there is no coupons
 */
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewListOfCoupons() {
    val navController: NavHostController = rememberNavController()
    val couponsViewModel: CouponViewModels = viewModel()
    val coupons = couponsViewModel.allCoupons.observeAsState()
    val actualSearch = remember { mutableStateOf("") }
    val viewType = remember { mutableStateOf(ViewType.GRID) }
    ListOfCoupons(navController, coupons, actualSearch, viewType)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListOfCoupons(
    navController: NavController,
    coupons: State<List<Coupon>?>,
    actualSearch: MutableState<String>,
    viewType: MutableState<ViewType>
) {
    Column {
        Spacer(modifier = Modifier.height(15.dp))

        val listingList: List<Coupon>? = coupons.value?.filter { c ->
            c.brandGasStation.uppercase().contains(actualSearch.value.uppercase())
        }

        if (listingList != null && listingList.isNotEmpty()) {

            if (viewType.value == ViewType.LIST) {
                LazyColumn {
                    items(listingList) { coupon ->
                        CouponListElement(coupon, navController)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2))
                {
                    items(listingList) { coupon ->
                        CouponGridListElement(coupon, navController)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        } else {
            TitleSmall(
                text = stringResource(id = R.string.without_coupons),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@Preview
@Composable
fun PreviewCouponListElement() {
    val navController: NavHostController = rememberNavController()
    val coupon = Coupon(
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
    CouponListElement(coupon, navController)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CouponListElement(coupon: Coupon, navController: NavController) {
    val couponTypeString = if (coupon.type == CouponType.PERLITER) {
        stringResource(id = R.string.discount_per_litre)
    } else {
        stringResource(id = R.string.discount_per_total_value)
    }

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            navController.navigate("coupon_details/${coupon.couponId}")
        }
    ) {
        Row(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Column {
                LabelMedium(
                    text = coupon.brandGasStation,
                    color = MaterialTheme.colorScheme.onPrimary,
                    weight = FontWeight.Bold
                )
                LabelMedium(
                    text = coupon.discountVale.toString() + " €",
                    color = MaterialTheme.colorScheme.onPrimary
                )
                LabelSmall(
                    text = couponTypeString,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LabelSmall(
                    text = stringResource(id = R.string.is_valid),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(5.dp))
                if (coupon.valid == true) {
                    Image(
                        painter = painterResource(id = R.drawable.checked),
                        contentDescription = "image",
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.danger),
                        contentDescription = "image",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCouponGridListElement() {
    val navController: NavHostController = rememberNavController()
    val coupon = Coupon(
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
    CouponGridListElement(coupon, navController)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CouponGridListElement(coupon: Coupon, navController: NavController) {
    val couponTypeString = if (coupon.type == CouponType.PERLITER) {
        stringResource(id = R.string.discount_per_litre)
    } else {
        stringResource(id = R.string.discount_per_total_value)
    }

    Card(
        shape = RoundedCornerShape(15.dp),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .width(40.dp)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        onClick = {
            navController.navigate("coupon_details/${coupon.couponId}")
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                LabelMedium(
                    text = coupon.brandGasStation,
                    color = MaterialTheme.colorScheme.onPrimary,
                    weight = FontWeight.Bold
                )
                LabelMedium(
                    text = coupon.discountVale.toString() + " €",
                    color = MaterialTheme.colorScheme.onPrimary
                )
                LabelSmall(
                    text = couponTypeString,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(10.dp))
            LabelSmall(
                text = stringResource(id = R.string.is_valid),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (coupon.valid == true) {
                Image(
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = "image",
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.danger),
                    contentDescription = "image",
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}