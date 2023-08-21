package pt.ipp.estg.myapplication.models.database.coupon

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.State
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.models.firebase.StorageRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CouponViewModels(application: Application) : AndroidViewModel(application) {
    private val repository: CouponRepository
    private val storageRepository: StorageRepository = StorageRepository()

    var allCoupons: LiveData<List<Coupon>> = MutableLiveData()

    lateinit var expiringToday: LiveData<List<Coupon>>

    @SuppressLint("StaticFieldLeak")
    var context: Context

    init {
        val db = CouponDatabase.getDatabase(application)
        repository = CouponRepository(db.getCouponDao())
        allCoupons = repository.getCoupons()
        context = application.baseContext
        getCouponsToBeExpired()
    }

    fun insert(coupon: Coupon) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(coupon)
        }
        allCoupons = repository.getCoupons()
    }

    fun updateValidCoupons() {
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val currentDate = LocalDateTime.now().format(formatter)

        viewModelScope.launch(Dispatchers.IO) {
            allCoupons.value?.forEach { coupon ->
                coupon.valid =
                    ((((coupon.startDate != null) && (coupon.startDate <= currentDate)) || coupon.startDate == null) && (coupon.endDate != null && (coupon.endDate >= currentDate) || (coupon.endDate == null)))
            }
        }
    }

    fun getValidCoupons() {
        allCoupons = repository.getValidCoupons()
    }

    fun deleteCoupon(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id)
        }
    }

    fun getCouponById(id: Int): LiveData<Coupon> {
        return repository.getCouponById(id)
    }

    private fun replaceAllBy(newCoupons: List<Coupon>?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
            if (newCoupons != null) {
                for (item in newCoupons) {
                    repository.insert(item)
                }
            }
        }
    }

    fun getCouponsToBeExpired() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDateTime.now().format(formatter)

        expiringToday = repository.getCouponsExpiringToday(currentDate)
    }

    fun notifyNextCouponsToBeExpired(title: String, subText: String) {
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, "channelId")
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.coupon
                )
            )
            .setSmallIcon(R.drawable.compass_logo)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(subText)
            )
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel
        val name = "name"
        val descriptionText = "description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("channelId", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    fun uploadCouponsToCloudByUser(
        context: Context, email: String, coupons: State<List<Coupon>?>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("coupons clod", "${allCoupons.value}")
            coupons.value?.let {
                storageRepository.uploadCouponsToUser(email, it) { result ->
                    if (result) {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.upload_coupons_success),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    } else {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.upload_coupons_error),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                }
            }
        }
    }

    fun getCloudCoupons(
        email: String,
        context: Context,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            storageRepository.getCoupons(email, onError = {
                FancyToast.makeText(
                    context,
                    context.resources.getString(R.string.get_coupons_error),
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }, onSuccess = {
                if (it?.isEmpty() == true) {
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_coupons_empty),
                        FancyToast.LENGTH_LONG,
                        FancyToast.WARNING,
                        false
                    ).show()
                } else {
                    replaceAllBy(it)
                    FancyToast.makeText(
                        context,
                        context.resources.getString(R.string.get_coupons_success),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
            })
        }
    }

    fun removeVehicleCloudByUser(
        context: Context, email: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
                storageRepository.uploadCouponsToUser(email, listOf()) { result ->
                    if (result) {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.remove_coupons_success),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    } else {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.remove_coupons_error),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
            }
        }
    }
}