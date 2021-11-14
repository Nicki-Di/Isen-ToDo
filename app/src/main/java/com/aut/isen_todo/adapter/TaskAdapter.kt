package com.aut.isen_todo.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.aut.isen_todo.DBHelper
import com.aut.isen_todo.R
import com.aut.isen_todo.model.TaskModel
import com.google.android.material.card.MaterialCardView

class TaskAdapter(
    private val context: Context,
    private val tasksList: MutableList<TaskModel>
) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val cardview: MaterialCardView = view.findViewById(R.id.main_cardview)
        val checkbox: CheckBox = view.findViewById(R.id.task_checkbox)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_task_button)
        val task = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    fun updateCheckBoxTextStyle(holder: ItemViewHolder) {
        if (holder.checkbox.isChecked)
            holder.checkbox.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        else
            holder.checkbox.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = tasksList[position]

        val color = when(item.type){
            0 -> R.color.type0
            1 -> R.color.type1
            2 -> R.color.type2
            else -> R.color.type3
        }
        holder.cardview.strokeColor = context.getColor(color)

        holder.checkbox.text = item.title
        holder.checkbox.isChecked = when (item.done) {
            1 -> true
            else -> false
        }

        updateCheckBoxTextStyle(holder)

        Log.d("pm", item.id.toString())
        Log.d("pm", item.done.toString())

        holder.checkbox.setOnClickListener {
            val db = DBHelper(context, null)
            if (holder.checkbox.isChecked)
                db.updateTaskStatus(item, 1)
            else
                db.updateTaskStatus(item, 0)
            updateCheckBoxTextStyle(holder)
        }

        holder.deleteButton.setOnClickListener {
            val db = DBHelper(context, null)
            if (db.deleteTask(item) == 1) {
                tasksList.remove(item)
                notifyDataSetChanged()
            }
        }


    }

    override fun getItemCount() = tasksList.size
}