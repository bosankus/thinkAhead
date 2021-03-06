package tech.androidplay.sonali.todo.model

import com.google.firebase.firestore.DocumentId
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 5/6/2020, 5:30 AM
 */

data class Todo(
    @DocumentId
    var docId: String = "",
    @SerializedName("creator")
    var creator: String = "",
    @SerializedName("todoBody")
    var todoBody: String? = "",
    @SerializedName("todoDesc")
    var todoDesc: String? = "",
    @SerializedName("todoDate")
    var todoDate: String? = "",
    @SerializedName("todoCreationTimeStamp")
    var todoCreationTimeStamp: String = "",
    @JvmField
    @SerializedName("isCompleted")
    var isCompleted: Boolean = false,
    @SerializedName("taskImage")
    var taskImage: String? = "",
    @SerializedName("assignee")
    var assigneeList: List<*>? = null,
    @SerializedName("priority")
    var priority: Int? = 0,
) : Serializable