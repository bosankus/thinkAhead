package tech.androidplay.sonali.todo.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import tech.androidplay.sonali.todo.R
import tech.androidplay.sonali.todo.adapter.TodoListAdapter
import tech.androidplay.sonali.todo.data.TodoList
import tech.androidplay.sonali.todo.util.TimeStampUtil

class MainActivity : AppCompatActivity() {

    private val currentDate: String by lazy { TimeStampUtil().currentDate }
    private val currentTime: String by lazy { TimeStampUtil().currentTime }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }


        // load animations
        val btnAnimation = AnimationUtils.loadAnimation(this, R.anim.btn_animation)
        btnAddTodo.startAnimation(btnAnimation)

        showTodoList()

    }

    // TodoList recyclerview
    private fun showTodoList() {
        todoListRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val todo = ArrayList<TodoList>()
        todo.add(TodoList("1", "Get update from you broadband.", currentDate, currentTime))
        todo.add(TodoList("2", "Get update from you Office", currentDate, currentTime))
        val adapter = TodoListAdapter(todo)
        todoListRecyclerview.adapter = adapter
    }
}

