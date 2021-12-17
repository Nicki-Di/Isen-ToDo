package com.aut.isen_todo.model

import java.sql.Timestamp

class TaskModel(val id: Int, val title: String, var type: Int, var done: Int, var notifTime: Timestamp) {

}