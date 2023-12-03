package com.example.trainbookingsystem.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.databinding.TicketCheckHolderBinding

class HistoryRvAdapter(private val ticketList: List<TicketCheck>) :
    RecyclerView.Adapter<HistoryRvAdapter.CheckViewHolder>() {

    class CheckViewHolder(private val binding: TicketCheckHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var ticket: TicketCheck? = null
        fun bind(ticketCheck: TicketCheck) {
            binding.textViewDestFrom.text = ticketCheck.ticket.startDestination
            binding.textViewDestTo.text = ticketCheck.ticket.endDestination
            binding.textViewTimeFrom.text = ticketCheck.ticket.departureTime
            binding.textViewTimeTo.text = ticketCheck.ticket.arrivalTime
            val trainNo = "Train No ${ticketCheck.ticket.trainNum}"
            binding.textViewTrainNum.text = trainNo
            val price = "${ticketCheck.price} руб"
            binding.textViewPrice.text = price
            binding.textViewBoughtAt.text = ticketCheck.date
            ticket = ticketCheck
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckViewHolder {
        return CheckViewHolder(
            TicketCheckHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }

    override fun onBindViewHolder(holder: CheckViewHolder, position: Int) {
        holder.bind(ticketList[position])
    }
}