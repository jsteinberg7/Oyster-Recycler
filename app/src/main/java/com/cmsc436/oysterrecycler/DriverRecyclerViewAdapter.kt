package com.cmsc436.oysterrecycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseemailpasswordexample.databinding.ItemLayoutBinding

internal class DriverRecyclerViewAdapter(
    private val values: List<String>,
    private val owner: DriverFragment
) : RecyclerView.Adapter<DriverRecyclerViewAdapter.ViewHolder>() {

    /** Keeps track of single selection state. */
    private var lastCheckedPosition = -1
    private lateinit var lastCheckedView: TextView

    /**
     * Called [RecyclerView] to create a new [ViewHolder]
     * that contains an list item [View]. [onBindViewHolder]
     * will be called next.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            // Use the provided view binding class to inflate the item layout.
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Called by [RecyclerView] to bind item at [position] to the passed
     * [ViewHolder]. The [RecyclerView] maintains only a small number
     * of [ViewHolder] instances and therefore needs to recycle, or
     * rebind, those instances as the user scrolls new items into view.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = values[position]
    }

    /**
     * Called by [RecyclerView] when iterating through the list items.
     */
    override fun getItemCount(): Int {
        return values.size
    }

    fun select(index: Int) {
        if (lastCheckedPosition != index) {
            if (lastCheckedPosition != -1) {
                notifyItemChanged(lastCheckedPosition)
            }
            lastCheckedPosition = index
            notifyItemChanged(index)
        }
    }

    /**
     * A custom [ViewHolder] used to display each list item
     * and for handling button click input events.
     */
    inner class ViewHolder(binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var textView = binding.textView

        init {
            // Set a button click listener to notify the
            // owner when an item is clicked.
            textView.setOnClickListener {
                // Query the adapter position of this item.
                val position = values.indexOf(textView.text)

                // Only process click if this item is not already checked.
                if (position != lastCheckedPosition) {
                    // Clear the check state of the previously checked item.
                    if (lastCheckedPosition >= 0 && position != lastCheckedPosition) {
                        notifyItemChanged(lastCheckedPosition)
                        lastCheckedView.setBackgroundColor(Color.WHITE)
                    }
                    // Show the item in a checked state.
                    lastCheckedView = textView
                    lastCheckedPosition = position
                    textView.setBackgroundColor(
                        Color.argb(30,0,0,255))
                    // Notify owner that this item was clicked.
                    owner.onItemClick(position)
                }
            }
        }
    }
}
