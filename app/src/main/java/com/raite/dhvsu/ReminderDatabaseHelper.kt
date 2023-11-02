package com.raite.dhvsu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ReminderDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        public const val DATABASE_NAME = "reminder_database.db"
        private const val TABLE_REMINDER = "reminders"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = "CREATE TABLE $TABLE_REMINDER (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_TIME TEXT); "

        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    // CRUD Operations

    fun addReminder(reminder: Reminder): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, reminder.name)
        values.put(COLUMN_DESCRIPTION, reminder.description)
        values.put(COLUMN_TIME, reminder.time)

        return db.insert(TABLE_REMINDER, null, values)
    }
    fun updateReminder(reminder: Reminder): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, reminder.name)
        values.put(COLUMN_DESCRIPTION, reminder.description)
        values.put(COLUMN_TIME, reminder.time)


        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(reminder.id.toString())

        val updatedRows = db.update(TABLE_REMINDER, values, whereClause, whereArgs)

        return updatedRows > 0
    }



    fun getReminder(id: Long): Reminder? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_REMINDER, arrayOf(
                COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_TIME
            ), "$COLUMN_ID=?", arrayOf(id.toString()), null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return Reminder(
                    it.getLong(it.getColumnIndex(COLUMN_ID)),
                    it.getString(it.getColumnIndex(COLUMN_NAME)),
                    it.getString(it.getColumnIndex(COLUMN_DESCRIPTION)),
                    it.getString(it.getColumnIndex(COLUMN_TIME))
                )
            }
        }

        return null
    }

    fun getAllReminders(): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_REMINDER, arrayOf(
                COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_TIME
            ), null, null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val reminder = Reminder(
                    it.getLong(it.getColumnIndex(COLUMN_ID)),
                    it.getString(it.getColumnIndex(COLUMN_NAME)),
                    it.getString(it.getColumnIndex(COLUMN_DESCRIPTION)),
                    it.getString(it.getColumnIndex(COLUMN_TIME))
                    )
                reminders.add(reminder)
            }
        }

        return reminders
    }

    fun deleteReminder(reminder: Reminder): Boolean {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(reminder.id.toString())

        // Attempt to delete the reminder
        val deletedRows = db.delete(TABLE_REMINDER, whereClause, whereArgs)

        // Check if the deletion was successful
        if (deletedRows > 0) {
            return true // Deletion was successful
        }
        return false // Deletion failed
    }

}
