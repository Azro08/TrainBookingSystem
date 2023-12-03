package com.example.trainbookingsystem.presentation.add_tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.databinding.FragmentCreateTicketBinding
import com.example.trainbookingsystem.util.Constants
import com.example.trainbookingsystem.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateTicketFragment : Fragment() {
    private var _binding: FragmentCreateTicketBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTicketViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTicketBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSaveTicket.setOnClickListener {
            if (areAllFieldsFilled()) saveTicket()
            else Toast.makeText(
                requireContext(),
                getString(R.string.fill_upFields),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.imageButtonDateFrom.setOnClickListener {
            DateUtils.showDateTimePickerDialog(binding.textViewDateFrom, requireContext())
        }

        binding.imageButtonDateTo.setOnClickListener {
            DateUtils.showDateTimePickerDialog(binding.textViewDateTo, requireContext())
        }
    }


    private fun saveTicket() {
        val fromDest = binding.editTextFromDest.text.toString()
        val toDest = binding.editTextToDest.text.toString()
        val trainNo = binding.editTextTrainNum.text.toString().toInt()
        val fromDate = binding.textViewDateFrom.text.toString()
        val toDate = binding.textViewDateTo.text.toString()
        val price1 = binding.editTextPrice1.text.toString().toDouble()
        val price2 = binding.editTextPrice2.text.toString().toDouble()
        val price3 = binding.editTextPrice3.text.toString().toDouble()
        val price = listOf(price1, price2, price3)
        val seats = binding.editTextSeatsNum.text.toString().toInt()
        val freeSeats = mutableListOf<Int>()
        for (i in 1..seats) freeSeats.add(i)
        val id = Constants.generateRandomId()
        val ticket = Ticket(
            id = id,
            startDestination = fromDest,
            endDestination = toDest,
            trainNum = trainNo,
            departureTime = fromDate,
            arrivalTime = toDate,
            price = price,
            freeSeats = freeSeats
        )

        lifecycleScope.launch {
            viewModel.saveTicket(ticket)
            viewModel.ticketSavedState.collect { result ->
                if (result == "Done") findNavController().popBackStack()
                else
                    if (result.isNotEmpty()) Toast.makeText(
                        requireContext(),
                        result,
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    }

    private fun areAllFieldsFilled(): Boolean {
        val fromDest = binding.editTextFromDest.text.toString()
        val toDest = binding.editTextToDest.text.toString()
        val trainNo = binding.editTextTrainNum.text.toString()
        val fromDate = binding.textViewDateFrom.text.toString()
        val toDate = binding.textViewDateTo.text.toString()
        val price1 = binding.editTextPrice1.text.toString()
        val price2 = binding.editTextPrice2.text.toString()
        val price3 = binding.editTextPrice3.text.toString()
        val seats = binding.editTextSeatsNum.text.toString()

        return (fromDest.isNotEmpty() || toDest.isNotEmpty() || trainNo.isNotEmpty()
                || fromDate.isNotEmpty() || toDate.isNotEmpty() || price1.isNotEmpty() || price2.isNotEmpty() || price3.isNotEmpty() || seats.isNotEmpty())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}