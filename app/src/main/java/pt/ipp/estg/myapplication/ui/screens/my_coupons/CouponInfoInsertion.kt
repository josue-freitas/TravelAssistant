package pt.ipp.estg.myapplication.ui.screens.my_coupons

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.ui.screens.ui_components.FloatTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.IntTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalCheckBox
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Preview
@Composable
fun PreviewCouponInfoInsertion() {
    val couponType = remember { mutableStateOf(CouponType.PERLITER) }
    val brandGasStation = remember { mutableStateOf("GALP") }
    val discountValue = remember { mutableStateOf(0f) }
    val minSupplyValue = remember { mutableStateOf(0) }
    val minSupplyCheck = remember { mutableStateOf(true) }
    val maxSupplyValue = remember { mutableStateOf((0)) }
    val maxSupplyCheck = remember { mutableStateOf(true) }
    val dieselCheck = remember { mutableStateOf(true) }
    val glpCheck = remember { mutableStateOf(true) }
    val gasoline95Check = remember { mutableStateOf(true) }
    val gasoline98Check = remember { mutableStateOf(true) }
    val startDateCheck = remember { mutableStateOf(true) }
    val endDateCheck = remember { mutableStateOf(true) }
    val startDate = remember { mutableStateOf("") }
    val endDate = remember { mutableStateOf("") }
    val navController: NavHostController = rememberNavController()

    CouponInfoInsertion(
        couponType,
        brandGasStation,
        discountValue,
        minSupplyValue,
        minSupplyCheck,
        maxSupplyValue,
        maxSupplyCheck,
        dieselCheck,
        glpCheck,
        gasoline95Check,
        gasoline98Check,
        startDate,
        startDateCheck,
        endDate,
        endDateCheck,
        { },
        navController
    )
}

@Composable
fun CouponInfoInsertion(
    couponType: MutableState<CouponType>,
    brandGasStation: MutableState<String>,
    discountValue: MutableState<Float>,
    minSupplyValue: MutableState<Int>,
    minSupplyCheck: MutableState<Boolean>,
    maxSupplyValue: MutableState<Int>,
    maxSupplyCheck: MutableState<Boolean>,
    dieselCheck: MutableState<Boolean>,
    glpCheck: MutableState<Boolean>,
    gasoline95Check: MutableState<Boolean>,
    gasoline98Check: MutableState<Boolean>,
    startDate: MutableState<String>,
    startDateCheck: MutableState<Boolean>,
    endDate: MutableState<String>,
    endDateCheck: MutableState<Boolean>,
    addCoupon: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var tabRowStatus by rememberSaveable { mutableStateOf(0) }
    val listCouponType =
        listOf(
            stringResource(id = R.string.per_litre),
            stringResource(id = R.string.per_total_value)
        )
    val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val sdf = SimpleDateFormat("dd/MM/yyyy")
    val current: Date = sdf.parse(currentDate)

    //Log.d("Dte", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
    //validate
    data class IsDataValidReturnValue(val msg: String, val result: Boolean)


    fun isDataValid(): IsDataValidReturnValue {
        var result = true
        var msg = ""
        var endDateFormatted: Date = sdf.parse("01/01/2000")
        var startDateFormatted: Date = sdf.parse("01/01/2000")

        if (endDate.value != "") {
            endDateFormatted = sdf.parse(endDate.value)
        }
        if (startDate.value != "") {
            startDateFormatted = sdf.parse(startDate.value)
        }
        //Log.d('date', )

        if (brandGasStation.value.length < 2) {
            result = false
            msg = "Brand gas station should have at least 2 characters"
        } else if (discountValue.value <= 0f) {
            result = false
            msg = "The discount value should be superior than 0"
        } else if ((minSupplyCheck.value && minSupplyValue.value <= 0) || (maxSupplyCheck.value && maxSupplyValue.value <= 0)) {
            result = false
            msg = "The supply value should be superior than 0"
        } else if (minSupplyCheck.value && maxSupplyCheck.value && maxSupplyValue.value < minSupplyValue.value) {
            result = false
            msg = "The max supply value should be greater that the minimum"
        } else if (!dieselCheck.value && !glpCheck.value && !gasoline95Check.value && !gasoline98Check.value) {
            result = false
            msg = "The discount should be a applied at least a one gas type"
        } else if (startDateCheck.value && endDateCheck.value && (endDateFormatted.compareTo(
                startDateFormatted
            ) < 0)
        ) {
            result = false
            msg = "The end date should be greater than the start"
        } else if (endDateCheck.value && endDateFormatted.compareTo(current) < 0) {
            result = false
            msg = "The end date should be greater than the current date"
        }

        return IsDataValidReturnValue(msg, result)
    }

    LabelMedium(
        text = stringResource(id = R.string.whats_coupon_type),
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(20.dp))
    TabRow(
        selectedTabIndex =
        when (couponType.value) {
            CouponType.PERLITER -> 0
            CouponType.PERTOTALVALUE -> 1
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        listCouponType.forEachIndexed { index, title ->
            Tab(
                selected = tabRowStatus == index,
                onClick = {
                    tabRowStatus = index
                    when (index) {
                        0 -> couponType.value = CouponType.PERLITER
                        1 -> couponType.value = CouponType.PERTOTALVALUE
                    }
                }) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    LabelSmall(
                        text = title, color =
                        if (tabRowStatus == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))

    //brand gas station
    LabelMedium(
        text = stringResource(id = R.string.brand_gas_station),
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(10.dp))
    NormalTextField(
        stringState = brandGasStation,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("brand_gas_input"),
        errorMsg = stringResource(id = R.string.brand_gas_input),
        validation = { str -> str.length > 1 })
    Spacer(modifier = Modifier.height(20.dp))

    //discount value
    LabelMedium(
        text = stringResource(id = R.string.discount_amount),
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(10.dp))
    FloatTextField(
        numberState = discountValue, modifier = Modifier
            .fillMaxWidth()
            .testTag("discount_input")
    )
    Spacer(modifier = Modifier.height(20.dp))

    //Supply
    RowToDisplay(
        leftString = stringResource(id = R.string.minimum_supply),
        rightString = stringResource(id = R.string.max_supply),
        leftValue = minSupplyValue,
        rightValue = maxSupplyValue,
        leftCheckedValue = minSupplyCheck,
        rightCheckedValue = maxSupplyCheck
    )

    LabelMedium(
        text = stringResource(id = R.string.applied_gas_types),
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(15.dp))

    //diesel check
    RowCheck(stringResource(id = R.string.diesel), dieselCheck, testTag = "diesel_check")

    //gasoline 95 check
    RowCheck(stringResource(id = R.string.gasoline_95), gasoline95Check, testTag = "95_check")

    //gasoline 98 check
    RowCheck(stringResource(id = R.string.gasoline_98), gasoline98Check, testTag = "98_check")

    //glp check
    RowCheck(stringResource(id = R.string.lpg), glpCheck, testTag = "lpg_check")

    Spacer(modifier = Modifier.height(15.dp))

    LabelMedium(
        text = stringResource(id = R.string.expiration_date),
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(15.dp))

    //expiration dates
    RowCalendar(
        context,
        stringResource(R.string.start),
        stringResource(R.string.end),
        startDate,
        endDate,
        startDateCheck,
        endDateCheck
    )

    //PREVIOUS AND SUBMIT BUTTON
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedButton(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colorScheme.secondary,
            ),
            shape = RoundedCornerShape(15.dp),
            onClick = {
                navController.navigate("my_coupons_screen")
            }) {
            LabelMedium(
                text = stringResource(id = R.string.cancel),
                color = MaterialTheme.colorScheme.onSecondary
            )
        }


        OutlinedButton(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.success),
            ),
            modifier = Modifier.testTag("submit_button"),
            shape = RoundedCornerShape(15.dp),
            onClick = {
                val (msg, result) = isDataValid()
                if (result) {
                    addCoupon()
                    FancyToast.makeText(
                        context,
                        "Successful inserted!",
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    )
                    //Toast.makeText(ctx, "Bad input", Toast.LENGTH_LONG).show()
                } else {
                    FancyToast.makeText(
                        context,
                        msg,
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR,
                        false
                    ).show()
                }
            }) {
            LabelMedium(
                text = stringResource(id = R.string.submit),
                color = Color.White
            )
        }
    }
}


@Preview
@Composable
fun PreviewRowToDisplay() {
    val leftValue = remember { mutableStateOf(0) }
    val rightValue = remember { mutableStateOf(0) }
    val leftCheckedValue = remember { mutableStateOf(true) }
    val rightCheckedValue = remember { mutableStateOf(true) }

    RowToDisplay(
        "Left", "Right", leftValue, rightValue, leftCheckedValue, rightCheckedValue
    )
}

@Composable
fun RowToDisplay(
    leftString: String,
    rightString: String,
    leftValue: MutableState<Int>,
    rightValue: MutableState<Int>,
    leftCheckedValue: MutableState<Boolean>,
    rightCheckedValue: MutableState<Boolean>
) {
    //get actual size of screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.width((screenWidth / 2.5).dp)
        ) {
            //oil filter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = leftString,
                    color = MaterialTheme.colorScheme.primary
                )
                NormalCheckBox(
                    checked = leftCheckedValue.value,
                    onCheckedChange = { leftCheckedValue.value = !leftCheckedValue.value },
                    modifier = Modifier.testTag("min_supply_check")
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (leftCheckedValue.value) {
                IntTextField(
                    numberState = leftValue,
                    modifier = Modifier.fillMaxWidth(),
                    ignoreValue = 0
                )
            } else {
                Spacer(modifier = Modifier.height(58.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Column(
            Modifier.width((screenWidth / 2.5).dp)
        ) {
            //oil filter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = rightString,
                    color = MaterialTheme.colorScheme.primary
                )
                NormalCheckBox(
                    checked = rightCheckedValue.value,
                    modifier = Modifier.testTag("max_supply_check"),
                    onCheckedChange = { rightCheckedValue.value = !rightCheckedValue.value })
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (rightCheckedValue.value) {
                IntTextField(
                    numberState = rightValue,
                    modifier = Modifier.fillMaxWidth(),
                    ignoreValue = 0
                )
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun PreviewRowCheck() {
    val check = remember { mutableStateOf(true) }
    RowCheck(
        "Left", check
    )
}

@Composable
fun RowCheck(
    string: String,
    check: MutableState<Boolean>,
    testTag: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        LabelSmall(
            text = string,
            color = MaterialTheme.colorScheme.primary
        )
        NormalCheckBox(
            checked = check.value,
            modifier = Modifier.testTag(testTag),
            onCheckedChange = { check.value = !check.value })
    }
}

@Preview
@Composable
fun PreviewRowCalendar() {
    val context = LocalContext.current
    val leftValue = remember { mutableStateOf("") }
    val rightValue = remember { mutableStateOf("") }
    val leftCheckedValue = remember { mutableStateOf(true) }
    val rightCheckedValue = remember { mutableStateOf(true) }

    RowCalendar(
        context, "Left", "Right", leftValue, rightValue, leftCheckedValue, rightCheckedValue
    )
}


@Composable
fun RowCalendar(
    context: Context,
    leftString: String,
    rightString: String,
    leftValue: MutableState<String>,
    rightValue: MutableState<String>,
    leftCheckedValue: MutableState<Boolean>,
    rightCheckedValue: MutableState<Boolean>
) {
    //get actual size of screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.width((screenWidth / 2.5).dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = leftString,
                    color = MaterialTheme.colorScheme.primary
                )
                NormalCheckBox(
                    modifier = Modifier.testTag("start_check"),
                    checked = leftCheckedValue.value,
                    onCheckedChange = { leftCheckedValue.value = !leftCheckedValue.value })
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (leftCheckedValue.value) {
                Calendar(context, leftValue)
            } else {
                Spacer(modifier = Modifier.height(58.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Column(
            Modifier.width((screenWidth / 2.5).dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = rightString,
                    color = MaterialTheme.colorScheme.primary
                )
                NormalCheckBox(
                    checked = rightCheckedValue.value,
                    modifier = Modifier.testTag("end_check"),
                    onCheckedChange = { rightCheckedValue.value = !rightCheckedValue.value })
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (rightCheckedValue.value) {
                Calendar(context, rightValue)
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Preview
@Composable
fun PreviewCalendar() {
    val context = LocalContext.current
    val date = remember { mutableStateOf("") }
    Calendar(
        context, date,
    )
}

@Suppress("NAME_SHADOWING")
@Composable
fun Calendar(mContext: Context, date: MutableState<String>) {
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            Log.e("DAY", mDayOfMonth.toString())
            var string: String = if (mDayOfMonth < 10) {
                ("0$mDayOfMonth/").toString()
            } else {
                ("$mDayOfMonth/").toString()
            }
            string += if (mMonth + 1 < 10) {
                "0" + (mMonth + 1)
            } else {
                (mMonth + 1).toString()
            }
            date.value = "$string/$mYear"
        }, mYear, mMonth, mDay
    )

    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.secondary,
        ),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            mDatePickerDialog.show()
        }) {
        if (date.value == "") {
            LabelMedium(
                text = stringResource(id = R.string.select_date),
                color = MaterialTheme.colorScheme.onSecondary
            )
        } else {
            LabelMedium(
                text = date.value,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}