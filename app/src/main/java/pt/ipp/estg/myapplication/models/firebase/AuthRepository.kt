package pt.ipp.estg.myapplication.models.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pt.ipp.estg.myapplication.models.database.user.User

const val USER_COLLECTION = "users"

class AuthRepository {

    private val userRef: CollectionReference = Firebase.firestore.collection(USER_COLLECTION)

    private val currentUser = MutableLiveData(User())

    suspend fun createUser(user: User, password: String, onComplete: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            Firebase.auth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        createUserDocument(user) { success ->
                            if (success) {
                                Log.d("create user", "completed")
                                onComplete.invoke(true)
                            } else {
                                try {
                                    Firebase.auth.currentUser?.delete()
                                } catch (e: Exception) {
                                    Log.e("create user", "error deleting recent user")
                                }
                            }
                        }
                    } else {
                        onComplete.invoke(false)
                    }
                }
        }.await()
    }

    suspend fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            createUserDocument(user) { success ->
                if (success) {
                    Log.d("create user", "completed")
                    onComplete.invoke(true)
                } else {
                    try {
                        Firebase.auth.currentUser?.delete()
                    } catch (e: Exception) {
                        Log.e("create user", "error deleting recent user")
                    }
                }
            }
        }
    }

    suspend fun login(
        email: String,
        password: String,
        onComplete: (User?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            Firebase.auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        getUserDocument(
                            email,
                            onError = {
                                Log.e("login", it.toString())
                                onComplete.invoke(null)
                            }
                        ) { user ->
                            currentUser.postValue(user)
                            onComplete.invoke(user)
                        }
                    } else {
                        onComplete.invoke(null)
                    }
                }.await()
        }
    }

    private fun createUserDocument(
        user: User,
        onComplete: (Boolean) -> Unit,
    ) {
        userRef
            .document(user.email)
            .set(user)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    private fun getUserDocument(
        email: String,
        onError: (Exception) -> Unit,
        onSuccess: (User?) -> Unit
    ) {
        userRef
            .document(email)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(User::class.java))
            }
            .addOnFailureListener {
                onError.invoke(it)
            }
    }
}