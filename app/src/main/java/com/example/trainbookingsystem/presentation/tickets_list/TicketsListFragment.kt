package com.example.trainbookingsystem.presentation.tickets_list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.databinding.FragmentTicketsListBinding
import com.example.trainbookingsystem.util.Constants
import com.example.trainbookingsystem.util.DateUtils
import com.example.trainbookingsystem.util.ScreenState
import com.example.trainbookingsystem.util.UsersManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TicketsListFragment : Fragment() {
    private var _binding: FragmentTicketsListBinding? = null
    private val binding get() = _binding!!
    private var ticketsRvAdapter: TicketsListAdapter? = null
    private val viewModel: TicketsListViewModel by viewModels()

    @Inject
    lateinit var usersManager: UsersManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getAllTickets()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            Constants.getMainCities() // Replace this with your list of suggestions
        )
        binding.editTextFromDest.setAdapter(adapter)
        binding.editTextToDest.setAdapter(adapter)

        binding.buttonAddTicket.setOnClickListener {
            findNavController().navigate(R.id.nav_tickets_list_add_ticket)
        }

        if (usersManager.getRole() == Constants.USER) binding.buttonAddTicket.visibility = View.GONE

        binding.imageButtonDateFrom.setOnClickListener {
            DateUtils.showDateTimePickerDialog(binding.textViewDateFrom, requireContext())
        }

        binding.imageButtonDateTo.setOnClickListener {
            DateUtils.showDateTimePickerDialog(binding.textViewDateTo, requireContext())
        }

        binding.buttonUpdate.setOnClickListener {
            getAllTickets()
            cheapestTicketIndex = -1
            binding.editTextToDest.setText("")
            binding.editTextFromDest.setText("")
            binding.textViewDateFrom.text = ""
            binding.textViewDateTo.text = ""
        }

        binding.buttonSearch.setOnClickListener {
            binding.loadingGig.visibility = View.VISIBLE
            binding.rvTickets.visibility = View.GONE
            getFilteredTickets()
        }

        binding.imageButtonFilter.setOnClickListener {
            showFilterDialog()
        }

    }

    private fun showFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Filter Tickets")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                // Apply filter
                applyFilter(dialogView)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun applyFilter(dialogView: View) {
        val minPrice = dialogView.findViewById<EditText>(R.id.editTextMinPrice).text.toString()
            .toDoubleOrNull() ?: 0.0
        val maxPrice = dialogView.findViewById<EditText>(R.id.editTextMaxPrice).text.toString()
            .toDoubleOrNull()
        val minDuration =
            dialogView.findViewById<EditText>(R.id.editTextMinDuration).text.toString()
                .toIntOrNull() ?: 0
        val maxDuration =
            dialogView.findViewById<EditText>(R.id.editTextMaxDuration).text.toString()
                .toIntOrNull()

        val filteredTickets =
            viewModel.getFilteredTickets(minPrice, maxPrice, minDuration, maxDuration)
        Log.d("TicketsListAdapter", "Cheapest ticket ${getCheapestTicketIndex(filteredTickets)}")
        ticketsRvAdapter?.highlightCheapestTicket(
            getCheapestTicketIndex(filteredTickets),
            filteredTickets
        )
        display(filteredTickets)
    }

    private fun getCheapestTicketIndex(tickets: List<Ticket>): Int {
        var cheapestTicketIndex = -1
        var cheapestTicketPrice = Double.MAX_VALUE
        for ((index, ticket) in tickets.withIndex()) {
            if (ticket.price[0] < cheapestTicketPrice) {
                cheapestTicketPrice = ticket.price[0]
                cheapestTicketIndex = index
            }
        }
        return cheapestTicketIndex
    }

    private fun getAllTickets() {
        lifecycleScope.launch {
            if (usersManager.getRole() == Constants.ADMIN) viewModel.getAllTickets()
            else viewModel.getAllActiveTickets()
            viewModel.ticketsList.collect { state ->
                processResponse(state)
            }
        }
    }

    private fun processResponse(state: ScreenState<List<Ticket>?>) {
        when (state) {
            is ScreenState.Loading -> {}
            is ScreenState.Error -> {
                handleError()
            }

            is ScreenState.Success -> {
                binding.loadingGig.visibility = View.GONE
                binding.rvTickets.visibility = View.VISIBLE
                binding.textViewError.visibility = View.GONE

                if (!state.data.isNullOrEmpty()) display(state.data)
                else handleError()
            }
        }
    }


    private fun getFilteredTickets() {
        val startDest = binding.editTextFromDest.text.toString()
        val endDest = binding.editTextToDest.text.toString()
        var departureTime = binding.textViewDateFrom.text.toString()
        var arrivalTime = binding.textViewDateTo.text.toString()
        if (departureTime.isEmpty()) departureTime = ""
        if (arrivalTime.isEmpty()) arrivalTime = ""

        lifecycleScope.launch {
            viewModel.getTickets(startDest, endDest, departureTime, arrivalTime)
            viewModel.ticketsList.collect { state ->
                processResponse(state)
            }
        }

    }

    private fun display(ticketList: List<Ticket>) {
        ticketsRvAdapter = TicketsListAdapter(
            context = requireContext(),
            tickets = ticketList.toMutableList(),
            listener = { navToBuyTicket(it.id) },
            deleteListener = { deleteTicket(it.id) })
        binding.rvTickets.setHasFixedSize(true)
        binding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTickets.adapter = ticketsRvAdapter

        if (usersManager.getRole() == Constants.ADMIN) ticketsRvAdapter!!.addSwipeToDelete(binding.rvTickets)

    }

    private fun navToBuyTicket(ticketId: String) {
        if (usersManager.getRole() == Constants.USER)
            findNavController().navigate(
                R.id.nav_list_buy_ticket,
                bundleOf(Pair(Constants.TICKET_ID, ticketId))
            )
    }

    private fun deleteTicket(id: String) {
        lifecycleScope.launch {
            viewModel.deleteTicket(id)
        }
    }

    private fun handleError() {
        binding.loadingGig.visibility = View.GONE
        binding.rvTickets.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
    }

//    private fun areAllFieldsFilled(): Boolean {
//        val startDest = binding.editTextFromDest.text.toString()
//        val endDest = binding.editTextToDest.text.toString()
//        val departureTime = binding.textViewDateFrom.text.toString()
//        val arrivalTime = binding.textViewDateTo.text.toString()
//
//        return when {
//            startDest.isNotEmpty() && endDest.isNotEmpty() -> {
//                if (departureTime.isNotEmpty() && arrivalTime.isEmpty()) {
//                    false // Arrival time is required if departure time is specified
//                } else !(arrivalTime.isNotEmpty() && departureTime.isEmpty())
//            }
//
//            else -> true // If start and/or end destinations are empty, ignore time validations
//        }
//    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}