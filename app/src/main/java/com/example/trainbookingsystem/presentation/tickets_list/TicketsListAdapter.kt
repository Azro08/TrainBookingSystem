package com.example.trainbookingsystem.presentation.tickets_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.model.priceListToString
import com.example.trainbookingsystem.databinding.TicketHolderBinding
import com.example.trainbookingsystem.util.Constants

class TicketsListAdapter(
    private val role: String,
    private val tickets: List<Ticket>,
    private val listener: (ticket: Ticket) -> Unit,
    private val deleteListener: (ticket: Ticket) -> Unit
) : RecyclerView.Adapter<TicketsListAdapter.TicketViewHolder>() {

    class TicketViewHolder(
        private val role: String,
        listener: (ticket: Ticket) -> Unit,
        deleteListener: (ticket: Ticket) -> Unit,
        private val binding: TicketHolderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var ticket: Ticket? = null
        fun bind(currentTicket: Ticket) {
            if (role == Constants.USER) binding.buttonDeleteTicket.visibility = View.GONE
            binding.textViewDestFrom.text = currentTicket.startDestination
            binding.textViewDestTo.text = currentTicket.endDestination
            binding.textViewTimeFrom.text = currentTicket.departureTime
            binding.textViewTimeTo.text = currentTicket.arrivalTime
            val trainNo = "Train No ${currentTicket.trainNum}"
            binding.textViewTrainNum.text = trainNo
            val price = currentTicket.price.priceListToString()
            binding.textViewPrice.text = price
            ticket = currentTicket
        }

        init {
            binding.root.setOnClickListener { listener(ticket!!) }
            binding.buttonDeleteTicket.setOnClickListener { deleteListener(ticket!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder(
            role,
            listener,
            deleteListener,
            TicketHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

}