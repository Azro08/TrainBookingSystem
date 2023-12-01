package com.example.trainbookingsystem.presentation.buy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.databinding.SeatItemBinding

class SeatsRvAdapter(
    private val seatsList: List<Int>,
    private val listener: (seatNo: Int) -> Unit,
) : RecyclerView.Adapter<SeatsRvAdapter.SeatsViewHolder>() {

    private var lastClickedIndex: Int = -1

    class SeatsViewHolder(
        private var adapter: SeatsRvAdapter,
        listener: (seatNo: Int) -> Unit,
        private val binding: SeatItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var seat: Int = 0
        fun bind(seatNo: Int, position: Int) {
            binding.textViewSeatItem.text = seatNo.toString()
            if (adapter.lastClickedIndex == position) {
                binding.root.setBackgroundResource(R.drawable.rounded_edges_background)
                binding.textViewSeatItem.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.pink
                    )
                )
            } else {
                binding.root.setBackgroundResource(R.drawable.transparent_rounded_edges_background)
                binding.textViewSeatItem.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.darkBlue
                    )
                )
            }
            seat = seatNo
        }

        init {
            binding.root.setOnClickListener {
                listener(seat)
                if (adapter.lastClickedIndex != -1) {
                    adapter.notifyItemChanged(adapter.lastClickedIndex)
                }

                // Update the background for the currently clicked item
                adapter.lastClickedIndex = adapterPosition
                adapter.notifyItemChanged(adapterPosition)

                // Notify the listener
                adapter.listener(seat)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatsViewHolder {
        return SeatsViewHolder(
            this,
            listener,
            SeatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return seatsList.size
    }

    override fun onBindViewHolder(holder: SeatsViewHolder, position: Int) {
        holder.bind(seatsList[position], position)
    }

}