package tech.androidplay.sonali.todo.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_task_edit.*
import tech.androidplay.sonali.todo.R
import tech.androidplay.sonali.todo.data.viewmodel.TaskViewModel
import tech.androidplay.sonali.todo.utils.Constants.TASK_DOC_ID
import tech.androidplay.sonali.todo.utils.ResultData
import tech.androidplay.sonali.todo.utils.UIHelper.logMessage
import tech.androidplay.sonali.todo.utils.UIHelper.selectImage
import tech.androidplay.sonali.todo.utils.UIHelper.showSnack

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 24/Sep/2020
 * Email: ankush@androidplay.in
 */

@AndroidEntryPoint
class TaskEditFragment : Fragment(R.layout.fragment_task_edit) {

    private val taskViewModel: TaskViewModel by viewModels()
    private var taskId: String? = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskId = arguments?.getString(TASK_DOC_ID)
        setUpScreen()
        setListener()
        fetchTaskDetails(taskId!!)
    }

    private fun setUpScreen() {
        shimmerTaskEdit.startShimmer()
    }

    private fun setListener() {
        imgDeleteTask.setOnClickListener {
            taskViewModel.deleteTask(taskId)
            findNavController().navigate(R.id.action_taskEditFragment_to_taskFragment)
            showSnack(requireView(), "Task Deleted")
        }

        imgAttachFile.setOnClickListener { requireActivity().selectImage() }
    }

    private fun fetchTaskDetails(taskId: String) {
        taskViewModel.fetchTaskDetails(taskId).observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    is ResultData.Loading -> shimmerTaskEdit.visibility = View.VISIBLE
                    is ResultData.Success -> {
                        shimmerTaskEdit.visibility = View.GONE
                        clParentLayoutTaskEdit.visibility = View.VISIBLE
                        etTaskBody.setText(it.data?.todoBody)
                        etTaskDesc.setText(it.data?.todoDesc)
                    }
                    is ResultData.Failed -> {
                        shimmerTaskEdit.visibility = View.GONE
                        clErroLayoutTaskEdit.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

}