package com.example.trainbookingsystem.presentation.tickets_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    @Inject lateinit var usersManager: UsersManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getAllTickets()
        binding.buttonAddTicket.setOnClickListener {
            findNavController().navigate(R.id.nav_tickets_list_add_ticket)
        }

        if (usersManager.getRole() == Constants.USER) binding.buttonAddTicket.visibility = View.GONE

        binding.imageButtonDateFrom.setOnClickListener {
            Constants.showDateTimePickerDialog(binding.textViewDateFrom, requireContext())
        }

        binding.imageButtonDateTo.setOnClickListener {
            Constants.showDateTimePickerDialog(binding.textViewDateTo, requireContext())
        }

        binding.buttonSearch.setOnClickListener {
            if (areAllFieldsFilled()) {
                binding.loadingGig.visibility = View.VISIBLE
                binding.rvTickets.visibility = View.GONE
                getTickets()
            } else Toast.makeText(requireContext(), getString(R.string.fill_upFields), Toast.LENGTH_SHORT).show()
        }

    }

    private fun getAllTickets() {
        lifecycleScope.launch {
            viewModel.getAllTickets()
            viewModel.ticketsList.collect{state ->
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


    private fun getTickets() {
        val startDest = binding.editTextFromDest.text.toString()
        val endDest = binding.editTextToDest.text.toString()
        val date = binding.editTextFromDest.text.toString()

        lifecycleScope.launch {
            viewModel.getTickets(startDest, endDest, date)
            viewModel.ticketsList.collect { state ->
                processResponse(state)
            }
        }

    }

    private fun display(ticketList: List<Ticket>) {
        ticketsRvAdapter = TicketsListAdapter(ticketList) {
            findNavController().navigate(R.id.nav_list_buy_ticket, bundleOf(Pair(Constants.TICKET_ID, it.id)))
        }
        binding.rvTickets.setHasFixedSize(true)
        binding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTickets.adapter = ticketsRvAdapter
    }

    private fun handleError() {
        binding.loadingGig.visibility = View.GONE
        binding.rvTickets.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
    }

    private fun areAllFieldsFilled(): Boolean {
        val startDest = binding.editTextFromDest.text.toString()
        val endDest = binding.editTextToDest.text.toString()
        val date = binding.editTextFromDest.text.toString()
        return (startDest.isNotEmpty() && endDest.isNotEmpty() && date.isNotEmpty())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}