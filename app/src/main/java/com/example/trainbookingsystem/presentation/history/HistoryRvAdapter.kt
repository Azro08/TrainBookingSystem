package com.example.trainbookingsystem.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.databinding.TicketCheckHolderBinding
import com.example.trainbookingsystem.presentation.tickets_list.TicketsListAdapter

class HistoryRvAdapter(private val ticketList: List<TicketCheck>) :
    RecyclerView.Adapter<HistoryRvAdapter.CheckViewHolder>() {

    class CheckViewHolder(private val binding: TicketCheckHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var ticket: TicketCheck? = null
        fun bind(ticketCheck: TicketCheck) = with(binding) {
            textViewDestFrom.text = ticketCheck.ticket.startDestination
            textViewDestTo.text = ticketCheck.ticket.endDestination
            dateFrom.text = ticketCheck.ticket.departureTime.substringBefore(' ')
            timeFrom.text = ticketCheck.ticket.departureTime.substringAfter(' ') + TIME_TYPE
            dateTo.text = ticketCheck.ticket.arrivalTime.substringBefore(' ')
            timeTo.text = ticketCheck.ticket.departureTime.substringAfter(' ') + TIME_TYPE
            trainNum.text = "Train No ${ticketCheck.ticket.trainNum}"
            price.text = "${ticketCheck.price} руб \n${(ticketCheck.paymentType)}"
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

    companion object {
        private const val TIME_TYPE = "AM"
    }
}