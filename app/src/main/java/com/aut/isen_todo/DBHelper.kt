package com.aut.isen_todo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.aut.isen_todo.model.TaskModel
import java.sql.Timestamp

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                TITLE_COl + " TEXT," +
                TYPE_COL + " INTEGER," +
                DONE_COL + " INTEGER," +
                NTFTIME_COL + " TIMESTAMP" +
                ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addTask(task:TaskModel) {

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(TITLE_COl, task.title)
        values.put(TYPE_COL, task.type)
        values.put(DONE_COL, task.done)
        values.put(NTFTIME_COL, task.notifTime.time)


        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    fun deleteTask(task: TaskModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID_COL, task.id) // Task id
        // Deleting Row
        val success = db.delete(TABLE_NAME, ID_COL + "=" + task.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }

    fun updateTaskStatus(task: TaskModel, done: Int? =null, notifTime: Timestamp? =null): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID_COL, task.id) // Task id
        if (done != null)
            contentValues.put(DONE_COL, done) // Task State
        if (notifTime != null)
            contentValues.put(NTFTIME_COL, notifTime.time) // Task Notification Time
        // Updating Row Status
        val success = db.update(TABLE_NAME, contentValues, ID_COL + "=" + task.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }

    // below method is to get
    // all data from our database
    fun getTask(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)

    }

    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private const val DATABASE_NAME = "ISEN_TODO"

        // below is the variable for database version
        private const val DATABASE_VERSION = 1

        // below is the variable for table name
        const val TABLE_NAME = "tasks_table"

        // below is the variable for id column
        const val ID_COL = "id"

        // below is the variable for task title column
        const val TITLE_COl = "title"

        // below is the variable for task type column
        const val TYPE_COL = "type"

        // below is the variable for task done column
        const val DONE_COL = "done"

        // below is the variable for task notification time column
        const val NTFTIME_COL = "ntftime"
    }

    fun getReminderById(taskId: Long): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $ID_COL = '$taskId'"
        return db.rawQuery(query, null)
    }


}
