package tech.androidplay.sonali.todo.ui.fragment

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_task_bottom_sheet.*
import kotlinx.android.synthetic.main.frame_date_time_picker.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tech.androidplay.sonali.todo.R
import tech.androidplay.sonali.todo.data.viewmodel.TaskViewModel
import tech.androidplay.sonali.todo.utils.ResultData
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 5/3/2020, 12:22 PM
 */

@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment() {

    private val taskViewModel: TaskViewModel by viewModels()

    private lateinit var clCreateTask: ConstraintLayout
    private lateinit var swAlarm: Switch
    private lateinit var frameDateTimePicker: FrameLayout
    private lateinit var pickedTimeStamp: TextView
    private lateinit var pbOpenCalender: ProgressBar
    private lateinit var lottieCreateTask: LottieAnimationView
    private lateinit var btnCreateTask: Button

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_task_bottom_sheet, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)

        // initializing variables
        clCreateTask = view.findViewById(R.id.clCreateTask)
        swAlarm = view.findViewById(R.id.swAlarm)
        frameDateTimePicker = view.findViewById(R.id.frameDateTimePicker)
        pbOpenCalender = view.findViewById(R.id.pbOpenCalender)
        pickedTimeStamp = view.findViewById(R.id.tvSelectDateTimeDesc)
        lottieCreateTask = view.findViewById(R.id.lottiCreateTaskLoading)
        btnCreateTask = view.findViewById(R.id.btCreateTask)
        job = Job()

        // UI visibility
        frameDateTimePicker.visibility = View.GONE
        pbOpenCalender.visibility = View.GONE

        // setting click listeners
        clickListeners()

        return view
    }


    private fun clickListeners() {
        swAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val animationUtils =
                    AnimationUtils.loadAnimation(context, R.anim.fade_out_animation)
                frameDateTimePicker.startAnimation(animationUtils)
                frameDateTimePicker.visibility = View.VISIBLE
            } else if (!isChecked) {
                val animationUtils =
                    AnimationUtils.loadAnimation(context, R.anim.fade_in_animation)
                frameDateTimePicker.startAnimation(animationUtils)
                animationUtils.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        frameDateTimePicker.visibility = View.GONE
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                })
            }
        }

        frameDateTimePicker.setOnClickListener {
            job = showCalender()
        }


        btnCreateTask.setOnClickListener {

            if ((tvTaskInput.text.length) <= 0) tvTaskInput.error = "Cant't be empty mama!"
            else {
                clCreateTask.visibility = View.INVISIBLE
                lottieCreateTask.visibility = View.VISIBLE
                lottieCreateTask.playAnimation()

                createTask()
            }
        }
    }

    // Handling long running task- opening calender
    private fun showCalender() = CoroutineScope(Dispatchers.Main).launch {
        initiateDateTimePicker()
        pbOpenCalender.visibility = View.VISIBLE
    }

    // pick date & time of task to be notified
    private fun initiateDateTimePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        val datePicker = datePickerBuilder.build()
        datePicker.show(parentFragmentManager, datePicker.toString())
        datePicker.addOnCancelListener {
            pbOpenCalender.visibility = View.GONE
        }
        datePicker.addOnPositiveButtonClickListener {
            pbOpenCalender.visibility = View.GONE
            initiateTimePicker(datePicker.headerText)
        }
        datePicker.addOnNegativeButtonClickListener {
            pbOpenCalender.visibility = View.GONE
        }
    }

    // time picker
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun initiateTimePicker(date: String) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            pickedTimeStamp.text =
                date + ", " + SimpleDateFormat("HH:mm").format(cal.time).toString()
        }
        TimePickerDialog(
            context,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun createTask() {
        taskViewModel.createTask(
            tvTaskInput.text.toString(), tvSelectDateTimeDesc.text.toString()
        ).observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    is ResultData.Loading -> {
                    }
                    is ResultData.Success -> {
                    }
                    is ResultData.Failed -> {
                    }
                }
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        job.cancel()
    }
}