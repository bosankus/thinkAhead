package tech.androidplay.sonali.todo.utils.alarmutils

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import tech.androidplay.sonali.todo.data.firebase.FirebaseRepository
import tech.androidplay.sonali.todo.utils.Constants.TASK_DOC_ID
import javax.inject.Inject

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 14/Dec/2020
 * Email: ankush@androidplay.in
 */

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NotificationReceiver : Service() {

    @Inject
    lateinit var repository: FirebaseRepository

    @Inject
    lateinit var notificationManager: NotificationManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var taskId = ""
    private var notificationId = 0

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            taskId = "${it.getStringExtra(TASK_DOC_ID)}"
            notificationId = it.getIntExtra("notificationId", 0)
            notificationManager.cancel(notificationId)
        }
        changeTaskStatus(taskId)
        return START_REDELIVER_INTENT
    }

    private fun changeTaskStatus(taskId: String) {
        val map: Map<String, Boolean> = mapOf("isCompleted" to true)
        scope.launch { repository.updateTask(taskId, map) }

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}