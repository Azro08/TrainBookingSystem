package com.example.trainbookingsystem.presentation.admin.tickets_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.databinding.TicketHolderBinding

class TicketsListAdapter (
    private val tickets : List<Ticket>,
    private val listener : (ticket : Ticket) -> Unit) : RecyclerView.Adapter<TicketsListAdapter.TicketViewHolder>(){

    class TicketViewHolder(
        listener: (ticket: Ticket) -> Unit,
        private val binding : TicketHolderBinding
    ) : RecyclerView.ViewHolder(binding.root){
        private var ticket : Ticket? = null
        fun bind(currentTicket : Ticket){
            binding.textViewDestFrom.text = currentTicket.startDestination
            binding.textViewDestTo.text = currentTicket.endDestination
            binding.textViewTimeFrom.text = currentTicket.departureTime
            binding.textViewTimeTo.text = currentTicket.arrivalTime
            val trainNo = "Train No ${currentTicket.trainNum}"
            binding.textViewTrainNum.text = trainNo
            val price = "${currentTicket.price} руб"
            binding.textViewPrice.text = price
            ticket = currentTicket
        }

        init {
            binding.root.setOnClickListener {listener(ticket!!)}
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder(listener, TicketHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

}