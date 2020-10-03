package tech.androidplay.sonali.todo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tech.androidplay.sonali.todo.data.model.Todo
import tech.androidplay.sonali.todo.data.room.TaskDao
import tech.androidplay.sonali.todo.utils.ResultData
import tech.androidplay.sonali.todo.utils.UIHelper.getCurrentTimestamp
import javax.inject.Inject

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 5/6/2020, 4:54 AM
 */


class TaskRepository @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val taskDao: TaskDao,
    private val taskListRef: CollectionReference,
) {

    private val userDetails = firebaseAuth.currentUser

    private val query: Query = taskListRef
        .whereEqualTo("id", userDetails?.uid)
        .orderBy("todoCreationTimeStamp", Query.Direction.ASCENDING)

    suspend fun insertTask(todo: Todo) = taskDao.insertTask(todo)

    suspend fun deleteTask(todo: Todo) = taskDao.deleteTask(todo)

    fun getAllTasks() = taskDao.getTaskByCreationTime()

    suspend fun create(
        todoBody: String,
        todoDesc: String,
        todoReminder: String
    ): ResultData<Boolean> {
        val task = hashMapOf(
            "id" to userDetails?.uid,
            "todoBody" to todoBody,
            "todoDesc" to todoDesc,
            "todoReminder" to todoReminder,
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
    fun fetchTasksRealtime() = callbackFlow {
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

    suspend fun updateTask(taskId: String, map: Map<String, Any>) {
        try {
            taskListRef.document(taskId)
                .update(map)
                .await()
        } catch (e: Exception) {
        }
    }

    suspend fun deleteTask(docId: String) {
        try {
            taskListRef.document(docId)
                .delete()
                .await()
        } catch (e: Exception) {
        }
    }

    /*suspend fun uploadImage(uri: Uri): ResultData<String> {
        val ref = storageReference
            .child("${userDetails?.email}/${userDetails?.uid}")
        return try {
            ref.putFile(uri).await()
            val imageUrl = ref.downloadUrl.await().toString()
            ResultData.Success(imageUrl)
        } catch (e: Exception) {
            ResultData.Failed(e.message)
        }
    }*/
}



