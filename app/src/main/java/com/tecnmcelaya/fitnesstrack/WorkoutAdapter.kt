package com.tecnmcelaya.fitnesstrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(
    private val workoutList: MutableList<Workout>,
    private val onItemClicked: (Workout) -> Unit
) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tvWorkoutType)
        val tvDate: TextView = itemView.findViewById(R.id.tvWorkoutDate)
        val tvDuration: TextView = itemView.findViewById(R.id.tvWorkoutDuration)
        val tvCalories: TextView = itemView.findViewById(R.id.tvWorkoutCalories)
        val tvDistance: TextView = itemView.findViewById(R.id.tvWorkoutDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.tvType.text = workout.tipo
        holder.tvDate.text = workout.fecha
        holder.tvDuration.text = "${workout.duracion} min"
        holder.tvCalories.text = "${workout.calorias} kcal"

        if (workout.distancia != null) {
            holder.tvDistance.text = "${workout.distancia} km"
            holder.tvDistance.visibility = View.VISIBLE
        } else {
            holder.tvDistance.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClicked(workout)
        }
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }
}