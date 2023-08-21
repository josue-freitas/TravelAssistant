package pt.ipp.estg.myapplication.models.firebase

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.models.database.user.User

data class LoginUiState(
    val firebaseUser: FirebaseUser? = null,
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val loginError: String? = null,
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AuthRepository = AuthRepository()

    private val userPref =
        application.baseContext.getSharedPreferences("userPref", Context.MODE_PRIVATE)

    var currentUser: MutableLiveData<User> = MutableLiveData(User())

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    init {
        //check if there is a user saved locally
        viewModelScope.launch(Dispatchers.IO) {
            if (userPref.contains("email")) {
                val user = User(
                    email = userPref.getString("email", "")!!,
                    name = userPref.getString("name", "")!!,
                    birthDate = userPref.getString("birthDate", "")!!,
                    registeredDate = userPref.getString("registeredDate", "")!!,
                    contact = userPref.getInt("contact", 0),
                )
                currentUser.postValue(user)
            }
        }
    }

    fun createUser(context: Context, user: User, password: String) {
        loginUiState = loginUiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.createUser(user = user, password = password) { isSuccessful ->
                    if (isSuccessful) {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.registrated_successfully),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                        updateUserInPreferences(user)
                        loginUiState = loginUiState.copy(isSuccessLogin = true)
                    } else {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.wrong_login),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                        loginUiState = loginUiState.copy(isSuccessLogin = false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loginUiState = loginUiState.copy(isLoading = false)
            }
        }
    }

    fun loginUser(context: Context, email: String, password: String, onSuccess : () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loginUiState = loginUiState.copy(isLoading = true)
                loginUiState = loginUiState.copy(loginError = null)
                repository.login(email, password) { user ->
                    if (user != null) {
                        loginUiState =
                            loginUiState.copy(firebaseUser = FirebaseAuth.getInstance().currentUser)
                        loginUiState = loginUiState.copy(user = user)
                        updateUserInPreferences(user)
                        currentUser.value = user
                        loginUiState = loginUiState.copy(isSuccessLogin = true)
                        onSuccess.invoke()
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.logged_successfully),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    } else {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.wrong_login),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                        loginUiState = loginUiState.copy(isSuccessLogin = false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loginUiState = loginUiState.copy(isLoading = false)
            }
        }
    }

    fun updateUser(context: Context, user: User) {
        loginUiState = loginUiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateUser(user = user) { isSuccessful ->
                    if (isSuccessful) {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.updated_user),
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                        updateUserInPreferences(user)
                        loginUiState = loginUiState.copy(isSuccessLogin = true)
                    } else {
                        FancyToast.makeText(
                            context,
                            context.resources.getString(R.string.wrong_login),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                        loginUiState = loginUiState.copy(isSuccessLogin = false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loginUiState = loginUiState.copy(isLoading = false)
            }
        }
    }

    private fun updateUserInPreferences(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            //if not exists
            val editor = userPref.edit()
            editor.apply {
                editor.putString("email", user.email)
                editor.putString("name", user.name)
                editor.putString("birthDate", user.birthDate)
                editor.putString("registeredDate", user.registeredDate)
                editor.putInt("contact", user.contact)
                apply()
            }
            currentUser.postValue(user)
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseAuth.getInstance().signOut()
            loginUiState = loginUiState.copy(isSuccessLogin = false)
            userPref.edit().clear().apply();
            currentUser.postValue(User())
        }
    }
}
