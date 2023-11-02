package com.raite.dhvsu


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.raite.dhvsu.ReminderDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReminderListAdapter(private val dbHelper: ReminderDatabaseHelper, private val context: Context) : RecyclerView.Adapter<ReminderListAdapter.MedicationViewHolder>() {
    private var medications: List<Reminder> = emptyList()

    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvtodoName: TextView = itemView.findViewById(R.id.tvTodo)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnDelete: Button = itemView.findViewById(R.id.deletebtn)
        val btnEdit: Button = itemView.findViewById(R.id.editbtn)
        val imgIcon: ImageView = itemView.findViewById(R.id.imagetodo)



        // Add other views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.todoitem, parent, false)
        return MedicationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = medications[position]

        val sdf = SimpleDateFormat("M/dd/yyyy")
        val currentDate = sdf.format(Date())

        holder.tvtodoName.text = medication.name
        holder.tvTime.text = medication.time

        val todo = medication.name
        if(todo.contains("lunch") || todo.contains("tanghalian")){
            holder.imgIcon.setImageResource(R.drawable.raite_lunch)
            holder.tvDescription.text = "Lunch provides energy and nutrients to keep the body and brain working efficiently through the afternoon."
        }
        else if(todo.contains("breakfast") || todo.contains("almusal")){
            holder.imgIcon.setImageResource(R.drawable.raite_breakfast)
            holder.tvDescription.text = "Breakfast provides the body with important nutrients, to start the day feeling energized and nourished."
        }
        else if(todo.contains("drink") || todo.contains("water") || todo.contains("tubig")){
            holder.imgIcon.setImageResource(R.drawable.raite_water)
            holder.tvDescription.text = "Your body is composed of about 60% water. So, don't forget to hydrate!"
        }
        else if(todo.contains("dinner") || todo.contains("hapunan")){
            holder.imgIcon.setImageResource(R.drawable.raite_dinner)
            holder.tvDescription.text = "Eating an early and light dinner helps to improve sleep, improves digestion, boosts metabolism and also reduces blood pressure, keeping you healthy."
        }
        else if(todo.contains("sleep") || todo.contains("tulog")){
            holder.imgIcon.setImageResource(R.drawable.raite_sleep)
            holder.tvDescription.text = "During sleep, the body produces growth hormones necessary for development in children and adolescents. "
        }
        else if(todo.contains("wake up") || todo.contains("gising") || todo.contains("wakeup")){
            holder.imgIcon.setImageResource(R.drawable.raite_wakeup)
            holder.tvDescription.text = "Wake up and bring your own sunshine."
        }
        else if(todo.contains("gym") || todo.contains("workout") || todo.contains("exercise")){
            holder.imgIcon.setImageResource(R.drawable.raite_exercise)
            holder.tvDescription.text = "Being physically active can improve your brain health, help manage weight, reduce the risk of disease, strengthen bones and muscles, and improve your ability to do everyday activities."
        }
        else if(todo.contains("medicine") || todo.contains("gamot") || todo.contains("maintenance") || todo.contains("vitamins ")){
            holder.imgIcon.setImageResource(R.drawable.raite_medicine)
            holder.tvDescription.text = "The benefits of medicines are the helpful effects you get when you use them, such as lowering blood pressure, curing infection, or relieving pain."
        }
        else if(todo.contains("rest") || todo.contains("pahinga") || todo.contains("break") || todo.contains("pause")){
            holder.imgIcon.setImageResource(R.drawable.raite_rest)
            holder.tvDescription.text = "If you are experiencing joint pain, cramping, headaches, and muscle fatigue, it could be a sign that your body is breaking down and needs some rest to recover."

        }
        else if(todo.contains("meditation") || todo.contains("meditate")){
            holder.imgIcon.setImageResource(R.drawable.raite_meditation)
            holder.tvDescription.text = " In mindfulness meditation, you pay attention to your thoughts as they pass through your mind. You don't judge the thoughts or become involved with them."

        }
        else if(todo.contains("study") || todo.contains("aral")){
            holder.imgIcon.setImageResource(R.drawable.raite_study)
            holder.tvDescription.text = "Learning new skills can also improve your mental wellbeing by: boosting self-confidence and raising self-esteem."
        }
        else{
            holder.imgIcon.setImageResource(R.drawable.logonobg)
            holder.tvDescription.text = "Take care of your body. It’s the only place you have to live."
        }



        holder.btnDelete.setOnClickListener {
            val deletedMedication = medications[position]

            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Confirm Deletion")
            alertDialog.setMessage("Are you sure you want to delete this item?")

            alertDialog.setPositiveButton("Yes") { _, _ ->
                if (dbHelper.deleteReminder(deletedMedication)) {
                    // Delete the item from the list and update the RecyclerView
                    val newList = medications.toMutableList()
                    newList.remove(deletedMedication)
                    medications = newList.toList()
                    notifyItemRemoved(position)
                }
            }

            alertDialog.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close the dialog without deleting
            }

            alertDialog.show()
        }



        holder.btnEdit.setOnClickListener {
            val medicationToEdit = medications[position]

            val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_add_item, null)
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
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
                        val updatedMedication = Reminder(
                            id = medicationToEdit.id,
                            name = todoName,
                            description = description,
                            time = selectedTime

                        )
                        if (dbHelper.updateReminder(updatedMedication)) {
                            // Update the item in the list and update the RecyclerView
                            val updatedList = medications.toMutableList()
                            val index = updatedList.indexOfFirst { it.id == medicationToEdit.id }
                            if (index != -1) {
                                updatedList[index] = updatedMedication
                                medications = updatedList.toList()
                                notifyItemChanged(index)
                                scheduleNotification(todoName, selectedTime)
                            }

                            val alertDialog = AlertDialog.Builder(context)
                            alertDialog.setTitle("Success")
                            alertDialog.setMessage("Prescription has been edited.")

                            alertDialog.setPositiveButton("Close") { dialog, _ ->
                                dialog.dismiss()
                            }
                            alertDialog.show()
                            Toast.makeText(context, "Medication updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to update medication", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val etTodoName = dialogView.findViewById<EditText>(R.id.etTodo)
            val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
            val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)


            etTodoName.setText(medicationToEdit.name)
            etDescription.setText(medicationToEdit.description)




            val calendar = Calendar.getInstance()
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)

            alertDialog.show()
        }







        // Set click listeners or other actions as needed
    }

    override fun getItemCount(): Int {
        return medications.size
    }

    fun submitList(medications: List<Reminder>) {
        this.medications = medications
        notifyDataSetChanged()
    }

    private fun scheduleNotification(todoName: String, notificationTime: String) {
        val notificationIntent = Intent(context, ReminderNotificationReceiver::class.java)
        notificationIntent.putExtra("todoName", todoName)

        val requestCode = generateUniqueRequestCode() // Generate a unique request code

        val pendingIntent = PendingIntent.getBroadcast(
            context,
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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
