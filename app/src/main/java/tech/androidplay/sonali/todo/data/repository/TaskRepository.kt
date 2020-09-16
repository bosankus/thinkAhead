package tech.androidplay.sonali.todo.data.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import tech.androidplay.sonali.todo.data.model.Todo
import tech.androidplay.sonali.todo.data.model.User
import tech.androidplay.sonali.todo.utils.ResultData
import tech.androidplay.sonali.todo.utils.UIHelper.getCurrentTimestamp
import tech.androidplay.sonali.todo.utils.UIHelper.logMessage
import javax.inject.Inject

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 5/6/2020, 4:54 AM
 */

// Glide not loading dp . Upoload working with download url fetch


class TaskRepository @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val taskListRef: CollectionReference,
    private val storageReference: StorageReference
) {

    private val userId = firebaseAuth.currentUser?.uid

    private val query: Query = taskListRef
        .whereEqualTo("id", userId)
        .orderBy("todoCreationTimeStamp", Query.Direction.ASCENDING)

    private var user: User = User()

    suspend fun create(todoBody: String, todoDesc: String): ResultData<Boolean> {
        val task = hashMapOf(
            "id" to userId,
            "todoBody" to todoBody,
            "todoDesc" to todoDesc,
            "todoCreationTimeStamp" to getCurrentTimestamp(),
            "isCompleted" to false
        )

        return try {
            taskListRef
                .add(task)
                .await()
            ResultData.Success(true)
        } catch (e: Exception) {
            ResultData.Failed(false.toString())
        }

    }

    @ExperimentalCoroutinesApi
    fun fetchTasks() = callbackFlow {
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

    suspend fun completeTask(taskId: String, status: Boolean): MutableLiveData<Boolean> {
        val completeTaskLiveData: MutableLiveData<Boolean> = MutableLiveData()
        taskListRef.document(taskId)
            .update("isCompleted", status)
            .await()
        completeTaskLiveData.postValue(true)
        return completeTaskLiveData
    }

    fun uploadImage(uri: Uri) {
        val ref = storageReference.child("profilePicture/${userId}")
        val uploadTask = ref.putFile(uri)
        uploadTask.addOnCompleteListener {
            if (it.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    logMessage("Repo: $uri")
                    user.userDp = uri.toString()
                    // TODO: Store the URI in shared preference
                }
            }
        }
    }

    /*fun fetchTasks() = flow {
        emit(ResultData.Loading)
        val response = query.get().await().toObjects(Todo::class.java)
        emit(ResultData.Success(response))
    }*/
}



