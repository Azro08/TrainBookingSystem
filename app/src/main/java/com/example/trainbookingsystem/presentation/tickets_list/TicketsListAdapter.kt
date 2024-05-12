package com.example.trainbookingsystem.presentation.tickets_list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.databinding.TicketHolderBinding

var cheapestTicketIndex: Int = -1

class TicketsListAdapter(
    private val context: Context,
    private var tickets: MutableList<Ticket>,
    private val listener: (ticket: Ticket) -> Unit,
    private val deleteListener: (ticket: Ticket) -> Unit,
) : RecyclerView.Adapter<TicketsListAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(
        listener: (ticket: Ticket) -> Unit,
        private val binding: TicketHolderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var ticket: Ticket? = null
        fun bind(currentTicket: Ticket) = with(binding) {
            textViewDestFrom.text = currentTicket.startDestination
            textViewDestTo.text = currentTicket.endDestination
            dateFrom.text = currentTicket.departureTime.substringBefore(' ')
            timeFrom.text = currentTicket.departureTime.substringAfter(' ')
            dateTo.text = currentTicket.arrivalTime.substringBefore(' ')
            timeTo.text = currentTicket.arrivalTime.substringAfter(' ')
            val priceText = " ${currentTicket.price[0]} руб"
            textViewPrice.text = priceText
            val context = itemView.context
            val trainNumText = context.getString(R.string.train_no) + currentTicket.trainNum
            trainNum.text = trainNumText
            Log.d("TicketsListAdapter", "Cheapest ticket ad$cheapestTicketIndex}")

            if (adapterPosition == cheapestTicketIndex) {
                Log.d("TicketsListAdapter", "Cheapest ticket color${cheapestTicketIndex}")
                root.setBackgroundColor(
                    context.resources.getColor(
                        R.color.green,
                        context.theme
                    )
                )
            }

            ticket = currentTicket
        }

        init {
            binding.root.setOnClickListener { listener(ticket!!) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun highlightCheapestTicket(index: Int, filteredTickets: List<Ticket>) {
        Log.d("TicketsListAdapter", "Cheapest ticket new index $index")
        tickets = filteredTickets.toMutableList()
        cheapestTicketIndex = index
        Log.d("TicketsListAdapter", "Cheapest ticket chenged index $cheapestTicketIndex")
        notifyDataSetChanged()
    }


    fun addSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Enable left and right swipe
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedTicket = tickets[position]
                showDeleteConfirmationDialog(deletedTicket, position)
            }

        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDeleteConfirmationDialog(ticketToDelete: Ticket, position: Int) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_ticket))
            .setMessage(context.getString(R.string.delete_ticket_dialog))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                tickets.removeAt(position)
                notifyItemRemoved(position)
                deleteListener(ticketToDelete)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                // Restore the swiped item
                tickets.add(position, ticketToDelete)
                notifyItemInserted(position)
                notifyDataSetChanged()
                dialog.dismiss()
            }
            .show()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder(
            listener,
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