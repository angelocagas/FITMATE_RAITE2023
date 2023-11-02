package com.raite.dhvsu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.AlarmManager
import android.app.PendingIntent
import android.view.View
import android.widget.TextView
import com.raite.dhvsu.databinding.ActivityHomeBinding
import java.util.Date

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReminderListAdapter

    private val CHANNEL_ID = "TodoReminderChannel"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val dbHelper = ReminderDatabaseHelper(this)
        createNotificationChannel()

        val sdf = SimpleDateFormat("M/dd/yyyy")
        val currentDate = sdf.format(Date())

        // Initialize your RecyclerView and adapter
        recyclerView = binding.RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReminderListAdapter(dbHelper, this)
        recyclerView.adapter = adapter

       binding.tvDate.text = currentDate.toString()

        val medications = dbHelper.getAllReminders()
        adapter.submitList(medications)

        // Notify the adapter that the dataset has changed
        adapter.notifyDataSetChanged()










        binding.btnSave.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.activity_add_item, null)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Save") { dialog, _ ->
                    val etTodoName = dialogView.findViewById<EditText>(R.id.etTodo)
                    val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
                    val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)



                    val selectedHour = timePicker.hour
                    val selectedMinute = timePicker.minute

                    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    calendar.set(Calendar.MINUTE, selectedMinute)

                    val todoName = etTodoName.text.toString()
                    val description = etDescription.text.toString()
                    val selectedTime = format.format(calendar.time)





                    if (todoName.isNotBlank() && selectedTime.isNotBlank()) {
                        val dbHelper = ReminderDatabaseHelper(this)
                        val medication = Reminder(name = todoName, description = description, time = selectedTime)
                        val insertedId = dbHelper.addReminder(medication)
                        if (insertedId > 0) {
                            // Item added successfully
                            // Update the data source for your adapter
                            val medications = dbHelper.getAllReminders()
                            adapter.submitList(medications)

                            // Notify the adapter that the dataset has changed
                            adapter.notifyDataSetChanged()
                            scheduleNotification(todoName, selectedTime)

                            // Show a message or perform any other actions
                            val alertDialog = AlertDialog.Builder(this)
                            alertDialog.setTitle("Success")
                            alertDialog.setMessage("Item has been added.")

                            alertDialog.setPositiveButton("Close") { dialog, _ ->
                                dialog.dismiss()
                            }
                            alertDialog.show()




                        } else {
                            // Handle insertion failure
                            // You can display an error message or take other actions
                            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle validation errors
                        // You can display error messages to the user
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Settings -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Do you want to exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            // Handle user confirmation to exit the app
                            finish()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
            true
        }    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medication Reminders"
            val descriptionText = "Medication Reminder Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(todoName: String, notificationTime: String) {
        val notificationIntent = Intent(this, ReminderNotificationReceiver::class.java)
        notificationIntent.putExtra("todoName", todoName)

        val requestCode = generateUniqueRequestCode() // Generate a unique request code

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val calendar = Calendar.getInstance()

        // Parse the notificationTime and set the calendar to the desired time
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).parse(notificationTime)
        val selectedHour = time.hours
        val selectedMinute = time.minutes

        // Set the calendar to the next occurrence of the specified time
        if (calendar.get(Calendar.HOUR_OF_DAY) > selectedHour ||
            (calendar.get(Calendar.HOUR_OF_DAY) == selectedHour &&
                    calendar.get(Calendar.MINUTE) >= selectedMinute)) {
            // If the specified time has already passed for today, add one day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.set(Calendar.SECOND, 0)

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private val requestCodes = mutableSetOf<Int>()

    private fun generateUniqueRequestCode(): Int {
        var requestCode = 0
        while (requestCodes.contains(requestCode)) {
            requestCode++
        }
        requestCodes.add(requestCode)
        return requestCode
    }



}
