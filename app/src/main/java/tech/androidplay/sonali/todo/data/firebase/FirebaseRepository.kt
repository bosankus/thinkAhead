package tech.androidplay.sonali.todo.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.androidplay.sonali.todo.data.model.Todo
import tech.androidplay.sonali.todo.utils.Constants.FEEDBACK_COLLECTION
import tech.androidplay.sonali.todo.utils.Constants.FIRESTORE_COLLECTION
import tech.androidplay.sonali.todo.utils.ResultData
import javax.inject.Inject

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 5/6/2020, 4:54 AM
 */

@ExperimentalCoroutinesApi
class FirebaseRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storageReference: StorageReference,
    fireStore: FirebaseFirestore,
) : FirebaseApi {

    private val userDetails = firebaseAuth.currentUser
    private val taskListRef = fireStore.collection(FIRESTORE_COLLECTION)
    private val feedbackReference = fireStore.collection(FEEDBACK_COLLECTION)

    private val query: Query = taskListRef
        .whereEqualTo("id", userDetails?.uid)
        .orderBy("todoCreationTimeStamp", Query.Direction.ASCENDING)

    override suspend fun logInUser(email: String, password: String): ResultData<FirebaseUser> {
        return try {
            val response = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
            ResultData.Success(response.user)
        } catch (e: Exception) {
            ResultData.Failed(e.message)
        }
    }

    override suspend fun createAccount(email: String, password: String): ResultData<FirebaseUser> {
        return try {
            val response = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            ResultData.Success(response.user)
        } catch (e: Exception) {
            ResultData.Failed(e.message)
        }
    }

    override suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun createTaskWithImage(taskMap: HashMap<*, *>, uri: Uri): ResultData<String> {
        return try {
            val docRef = taskListRef
                .add(taskMap)
                .await()
            when (val url: ResultData<String> = uploadImage(uri, docRef.id)) {
                is ResultData.Success -> updateTask(docRef.id, mapOf("taskImage" to url.data))
                is ResultData.Failed -> ResultData.Failed("Task Created. Image Upload Failed. Please retry.")
                else -> ResultData.Failed("Something went wrong while uploading Image")
            }
            ResultData.Success(docRef.id)
        } catch (e: Exception) {
            ResultData.Failed(false.toString())
        }
    }

    override suspend fun createTaskWithoutImage(taskMap: HashMap<*, *>): ResultData<String> {
        return try {
            val docRef = taskListRef
                .add(taskMap)
                .await()
            ResultData.Success(docRef.id)
        } catch (e: Exception) {
            ResultData.Failed(false.toString())
        }
    }

    override suspend fun fetchTaskRealtime(): Flow<ResultData<MutableList<Todo>>> = callbackFlow {
        offer(ResultData.Loading)
        val querySnapshot = query
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                else if (!value?.isEmpty!!) {
                    val todo = value.toObjects(Todo::class.java)
                    offer(ResultData.Success(todo))
                } else offer(ResultData.Failed())
            }
        awaitClose {
            querySnapshot.remove()
        }
    }

    override suspend fun updateTask(taskId: String, map: Map<String, Any?>) {
        taskListRef.document(taskId)
            .update(map)
            .await()
    }

    override suspend fun deleteTask(docId: String) {
        taskListRef.document(docId)
            .delete()
            .await()
    }

    override suspend fun provideFeedback(hashMap: HashMap<String, String?>): ResultData<String> {
        return try {
            val feedback = feedbackReference
                .add(hashMap)
                .await()
            ResultData.Success(feedback.id)
        } catch (e: Exception) {
            ResultData.Failed(e.message)
        }
    }

    override suspend fun uploadImage(uri: Uri, docRefId: String): ResultData<String> {
        val ref = storageReference
            .child("${userDetails?.email}/$docRefId")
        return try {
            ref.putFile(uri).await()
            val imageUrl = ref.downloadUrl.await().toString()
            val newImgMap = mapOf("taskImage" to imageUrl)
            updateTask(docRefId, newImgMap)
            ResultData.Success(imageUrl)
        } catch (e: Exception) {
            ResultData.Failed(e.message)
        }
    }
}


