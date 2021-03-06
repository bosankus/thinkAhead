package tech.androidplay.sonali.todo.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tech.androidplay.sonali.todo.R
import tech.androidplay.sonali.todo.service.TaskStatusUpdateService
import tech.androidplay.sonali.todo.utils.Constants.ALARM_DESCRIPTION
import tech.androidplay.sonali.todo.utils.Constants.ALARM_ID
import tech.androidplay.sonali.todo.utils.Constants.ALARM_TEXT
import tech.androidplay.sonali.todo.utils.Constants.ANDROID_OREO
import tech.androidplay.sonali.todo.utils.Constants.DEVICE_ANDROID_VERSION
import tech.androidplay.sonali.todo.utils.Constants.NOTIFICATION_CHANNEL_ID
import tech.androidplay.sonali.todo.utils.Constants.NOTIFICATION_CHANNEL_NAME
import tech.androidplay.sonali.todo.utils.Constants.NOTIFICATION_ID
import javax.inject.Inject

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 07/Oct/2020
 * Email: ankush@androidplay.in
 */

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager
    private var alarmText = ""
    private var alarmDescription = ""
    private var taskId = ""

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.let {
            alarmText = "${it.extras?.get(ALARM_TEXT)}"
            alarmDescription = "${it.extras?.get(ALARM_DESCRIPTION)}"
            taskId = "${it.extras?.get(ALARM_ID)}"
        } ?: return
        context?.let {
            showNotification(it, alarmText, alarmDescription, taskId)
        }
    }


    /**
     * [For notification button action]
     * We will user uniqueNotificationId for request code for creating new pending intent
     * each time when multiple notifications are send. So that on action click, the pending
     * intent hits related extras.
     */
    private fun showNotification(
        context: Context,
        alarmText: String,
        alarmDescription: String,
        taskId: String
    ) {

        // Generates notification code taskId.hashCode()
        val uniqueNotificationId = taskId.hashCode()
        // Extras for notification action button
        val intent = Intent(context, TaskStatusUpdateService::class.java).also {
            it.putExtra(ALARM_ID, taskId)
            it.putExtra(NOTIFICATION_ID, uniqueNotificationId)
        }
        val pendingIntent = PendingIntent.getService(
            context, uniqueNotificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Creating notification channel
        if (DEVICE_ANDROID_VERSION >= ANDROID_OREO)
            createNotification(context, notificationManager)

        baseNotificationBuilder.apply {
            setContentTitle(alarmText)
            setContentText(alarmDescription)
            setStyle(NotificationCompat.BigTextStyle().bigText(alarmDescription))
            addAction(R.drawable.ic_notification, "Mark Complete", pendingIntent)
        }

        notificationManager.notify(uniqueNotificationId, baseNotificationBuilder.build())
    }

    /**
     * This method creates the notification channel with custom sound attribute
     */
    private fun createNotification(context: Context, notificationManager: NotificationManager) {
        val uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.noti_sound)

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build()

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lightColor = Color.YELLOW
            setSound(uri, attributes)
            enableVibration(true)
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}