package tech.androidplay.sonali.todo.model

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("user")
    var user: String?, // Wayne Dyer
    @SerializedName("topic")
    var topic: String?, // suggestions heading feedback.
    @SerializedName("description")
    var description: String? // suggestions description feedback.
)
