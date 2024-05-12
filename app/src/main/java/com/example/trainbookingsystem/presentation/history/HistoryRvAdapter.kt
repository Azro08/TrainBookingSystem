package com.example.trainbookingsystem.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.databinding.TicketCheckHolderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryRvAdapter(private val ticketList: List<TicketCheck>) :
    RecyclerView.Adapter<HistoryRvAdapter.CheckViewHolder>() {

    class CheckViewHolder(private val binding: TicketCheckHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var ticket: TicketCheck? = null
        fun bind(ticketCheck: TicketCheck) = with(binding) {
            val context = root.context
            if (isDepartureTimePassed(ticketCheck.ticket.departureTime)) root.alpha = 0.4f
            textViewDestFrom.text = ticketCheck.ticket.startDestination
            textViewDestTo.text = ticketCheck.ticket.endDestination
            dateFrom.text = ticketCheck.ticket.departureTime.substringBefore(' ')
            timeFrom.text = ticketCheck.ticket.departureTime.substringAfter(' ')
            dateTo.text = ticketCheck.ticket.arrivalTime.substringBefore(' ')
            timeTo.text = ticketCheck.ticket.departureTime.substringAfter(' ')
            val trainNumText = context.getString(R.string.train_no) + ticketCheck.ticket.trainNum
            trainNum.text = trainNumText
            val priceText = "${ticketCheck.price} руб \n${(ticketCheck.paymentType)}"
            price.text = priceText
            ticket = ticketCheck
        }

        private fun isDepartureTimePassed(departureTime: String): Boolean {
            val currentTime = Date()
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val departureDateTime = dateFormatter.parse(departureTime) ?: return false
            return currentTime.after(departureDateTime)
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