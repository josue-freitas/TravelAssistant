package pt.ipp.estg.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import pt.ipp.estg.myapplication.location.ApplicationViewModel
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.models.firebase.AuthViewModel
import pt.ipp.estg.myapplication.sensor.LightSensor
import pt.ipp.estg.myapplication.sensor.ViewModelSensor
import pt.ipp.estg.myapplication.ui.screens.home.HomeScreen
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.SplashScreen
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.ui.screens.welcome_screen.WelcomeScreen
import pt.ipp.estg.myapplication.ui.theme.MyApplicationTheme


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val applicationViewModel: ApplicationViewModel by viewModels()
    val generalViewModels: GeneralViewModels by viewModels()
    val preferencesViewModel: PreferencesViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    val vehicleViewModels: VehicleViewModels by viewModels()
    val couponsViewModel: CouponViewModels by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            val expiringCoupons = couponsViewModel.expiringToday.observeAsState()
            val windowSize = calculateWindowSizeClass(activity = this)
            val context = LocalContext.current

            LaunchedEffect(key1 = true) {
                if (expiringCoupons.value?.isEmpty() == false) {
                    couponsViewModel.notifyNextCouponsToBeExpired(
                        context.getString(R.string.coupons_close_expired),
                        context.getString(R.string.coupons_close_expired_sub_text)
                    )
                }
            }

            val location by applicationViewModel.getLocationLiveData().observeAsState()

            val lightSensor = LightSensor(context)
            val viewModelSensor: ViewModelSensor by viewModels()
            viewModelSensor.onInit(lightSensor)
            val isDark = viewModelSensor.isDark.observeAsState()

            val preferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
            //if it's the first time and still dont have the preference
            if (!preferences.contains("isToShowWelcomeScreen")) {
                val editor = preferences.edit()
                editor.apply {
                    editor.putBoolean("isToShowWelcomeScreen", true)
                    apply()
                }
            }
            val isToShowWelcomeScreen = remember {
                mutableStateOf(
                    preferences.getBoolean("isToShowWelcomeScreen", false)
                )
            }

            //THEME
            val actualTheme = preferencesViewModel.theme.observeAsState()
            Log.d("theme", "main ${actualTheme.value}")

            val language = preferencesViewModel.language.observeAsState()

            actualTheme.value?.let {
                MyApplicationTheme(actualTheme = it) {
                    TransparentSystemBars()
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp)
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "splash_screen"
                        ) {

                            composable("welcome_screen") {
                                WelcomeScreen(
                                    navController = navController,
                                    preferencesViewModel = preferencesViewModel
                                )
                            }

                            composable("splash_screen") {
                                //LANGUAGE
                                LaunchedEffect(key1 = true) {
                                    language.value?.let {
                                        val appLocale: LocaleListCompat =
                                            LocaleListCompat.forLanguageTags(language.value)
                                        AppCompatDelegate.setApplicationLocales(appLocale)
                                    }
                                }
                                SplashScreen(
                                    navController = navController,
                                    isToShowWelcomeScreen = isToShowWelcomeScreen.value
                                )
                            }

                            // Main Screen
                            composable("home") {
                                HomeScreen(
                                    preferencesViewModel = preferencesViewModel,
                                    generalViewModels = generalViewModels,
                                    currentLocation = location,
                                    authViewModel = authViewModel,
                                    vehicleViewModel = vehicleViewModels,
                                    couponViewModel = couponsViewModel,
                                    isDark = isDark,
                                    windowSize = windowSize.widthSizeClass
                                )
                            }
                        }
                    }
                    LaunchedEffect(key1 = true) {
                        prepLocationUpdates()
                    }
                }
            }
        }
    }

    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationUpdates()
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "GPS Unavailable", Toast.LENGTH_LONG).show()
            }
        }

    private fun requestLocationUpdates() {
        applicationViewModel.startLocationUpdates()
    }

    @Composable
    fun TransparentSystemBars() {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }
    }
}
