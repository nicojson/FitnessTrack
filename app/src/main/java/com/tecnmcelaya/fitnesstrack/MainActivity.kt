package com.tecnmcelaya.fitnesstrack

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private val userWorkouts = mutableMapOf<String, MutableList<Workout>>()
    }

    private lateinit var rvWorkouts: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var workoutList: MutableList<Workout>

    private lateinit var tvWelcomeMessage: TextView
    private lateinit var tvWorkoutsCount: TextView
    private lateinit var tvCaloriesCount: TextView
    private lateinit var tvAvgDuration: TextView
    private lateinit var btnAddWorkout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        tvWorkoutsCount = findViewById(R.id.tvWorkoutsCount)
        tvCaloriesCount = findViewById(R.id.tvCaloriesCount)
        tvAvgDuration = findViewById(R.id.tvAvgDuration)
        rvWorkouts = findViewById(R.id.rvWorkouts)
        btnAddWorkout = findViewById(R.id.btnAddWorkout)

        val username = intent.getStringExtra("USERNAME") ?: "Guest"
        tvWelcomeMessage.text = "Welcome, $username!"

        workoutList = userWorkouts.getOrPut(username) { getInitialWorkoutsForNewUser() }

        rvWorkouts.layoutManager = LinearLayoutManager(this)
        workoutAdapter = WorkoutAdapter(workoutList) { workout ->
            showWorkoutDetailsDialog(workout)
        }
        rvWorkouts.adapter = workoutAdapter

        updateStats()

        btnAddWorkout.setOnClickListener {
            showAddWorkoutDialog()
        }
    }

    private fun getInitialWorkoutsForNewUser(): MutableList<Workout> {
        // Corrected parameter order and types
        return mutableListOf(
            Workout("Running", "Nov 11, 2025", 30, 300, "5.0 km"),
            Workout("Cycling", "Nov 10, 2025", 45, 400, "15.0 km"),
            Workout("Swimming", "Nov 09, 2025", 60, 500, "2.0 km"),
            Workout("Yoga", "Nov 08, 2025", 40, 150, null),
            Workout("Weight Training", "Nov 07, 2025", 50, 350, null)
        )
    }

    private fun showWorkoutDetailsDialog(workout: Workout) {
        val detailsMessage = """
            Type: ${workout.tipo}
            Date: ${workout.fecha}
            Duration: ${workout.duracion} min
            Calories: ${workout.calorias} kcal
            ${if (workout.distancia != null) "Distance: ${workout.distancia}" else ""}
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Workout Details")
            .setMessage(detailsMessage)
            .setPositiveButton("Share") { dialog, _ ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out my workout!\n\n$detailsMessage")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share Workout via"))
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateStats() {
        val totalWorkouts = workoutList.size
        val totalCalories = workoutList.sumOf { it.calorias }
        val avgDuration = if (totalWorkouts > 0) workoutList.sumOf { it.duracion } / totalWorkouts else 0
        tvWorkoutsCount.text = totalWorkouts.toString()
        tvCaloriesCount.text = "$totalCalories kcal"
        tvAvgDuration.text = "$avgDuration min"
    }

    private fun showAddWorkoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_workout, null)
        val workoutTypes = arrayOf("Running", "Cycling", "Swimming", "Yoga", "Weight Training")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, workoutTypes)
        val actvWorkoutType = dialogView.findViewById<AutoCompleteTextView>(R.id.actvWorkoutType)
        actvWorkoutType.setAdapter(adapter)

        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    etDate.setText(sdf.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add New Workout")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val etDuration = dialogView.findViewById<TextInputEditText>(R.id.etDuration)
                val etCalories = dialogView.findViewById<TextInputEditText>(R.id.etCalories)
                val etDistance = dialogView.findViewById<TextInputEditText>(R.id.etDistance)

                val type = actvWorkoutType.text.toString().takeIf { it.isNotBlank() } ?: "Workout"
                val date = etDate.text.toString().takeIf { it.isNotBlank() } ?: SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                val duration = etDuration.text.toString().toIntOrNull() ?: 0
                val calories = etCalories.text.toString().toIntOrNull() ?: 0
                val distanceFloat = etDistance.text.toString().toFloatOrNull()
                val distanceString = distanceFloat?.let { "$it km" }

                // Corrected parameter order
                val newWorkout = Workout(type, date, duration, calories, distanceString)
                addWorkout(newWorkout)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun addWorkout(workout: Workout) {
        workoutList.add(0, workout)
        workoutAdapter.notifyItemInserted(0)
        rvWorkouts.scrollToPosition(0)
        updateStats()
    }
}
