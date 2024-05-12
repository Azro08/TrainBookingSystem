package com.example.trainbookingsystem.presentation.buy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trainbookingsystem.data.model.CardDetails
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.data.model.priceListToString
import com.example.trainbookingsystem.databinding.FragmentBuyTicketBinding
import com.example.trainbookingsystem.util.Constants
import com.example.trainbookingsystem.util.DateUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BuyTicketFragment : Fragment() {
    private var _binding: FragmentBuyTicketBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BuyTicketViewModel by viewModels()
    private var ticket: Ticket? = null
    private var seatsRvAdapter: SeatsRvAdapter? = null
    private var selectedSeat = 0
    private var price = 0.0
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyTicketBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ticketId = arguments?.getString(Constants.TICKET_ID) ?: ""
        getTicketDetails(ticketId)
        binding.buttonBuyTicket.setOnClickListener {
            binding.layoutBuyTicket.visibility = View.VISIBLE
        }

        binding.buttonSubmitTicket.setOnClickListener {
            if (allFieldsAreFilled()) {
                binding.buttonSubmitTicket.visibility = View.GONE
                binding.buttonLoadingGif.visibility = View.VISIBLE
                submitTicket()
            }
        }

    }

    private fun submitTicket() {
        val cardDetails = getCardDetails()
        val paymentMethod = getPaymentMethod()
        val userId = firebaseAuth.currentUser?.uid ?: ""
        val date = DateUtils.getCurrentDateString()
        val id = Constants.generateRandomId()
        if (ticket == null) ticket = Ticket()
        val ticketCheck = TicketCheck(
            id,
            userId,
            ticket!!,
            date,
            price,
            paymentMethod,
            cardDetails,
            seatNumber = selectedSeat
        )

        lifecycleScope.launch {
            viewModel.buyTicket(ticketCheck)
            viewModel.buyTicketState.collect {
                if (it == "Done") findNavController().popBackStack()
                else {
                    binding.buttonSubmitTicket.visibility = View.VISIBLE
                    binding.buttonLoadingGif.visibility = View.GONE
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun getCardDetails(): CardDetails {
        return if (getPaymentMethod() == "Cash") CardDetails()
        else {
            val cardNum = binding.editTextCardNumber.text.toString().toInt()
            val cardExp = binding.editTextExpirationDate.text.toString()
            val cardCvv = binding.editTextCVV.text.toString().toInt()
            val cardHolder = binding.editTextCardHolderName.text.toString()
            CardDetails(cardNum, cardExp, cardCvv, cardHolder)
        }
    }

    private fun allFieldsAreFilled(): Boolean {
        val paymentMethod = getPaymentMethod()
        val cardNume = binding.editTextCardNumber.text.toString()
        val cardExp = binding.editTextExpirationDate.text.toString()
        val cardCvv = binding.editTextCVV.text.toString()
        val cardHolder = binding.editTextCardHolderName.text.toString()

        return !((paymentMethod == "Credit Card" && (cardNume.isEmpty() || cardCvv.isEmpty() || cardExp.isEmpty() || cardHolder.isEmpty())) || selectedSeat == 0)

    }

    private fun getPaymentMethod(): String {
        return if (binding.radioButtonCreditCard.isChecked) {
            "Credit Card"
        } else {
            "Cash"
        }
    }

    private fun getTicketDetails(ticketId: String) {
        lifecycleScope.launch {
            viewModel.getTicket(ticketId)
            viewModel.ticket.collect {
                if (it != null) {
                    ticket = it
                    displayTicketDetails(it)
                } else binding.root.visibility = View.GONE
            }
        }
    }

    private fun displayTicketDetails(ticket: Ticket) {
        binding.apply {
            root.visibility = View.VISIBLE
            textViewDestFrom.text = ticket.startDestination
            textViewDestTo.text = ticket.endDestination
            dateFrom.text = ticket.departureTime.substringBefore(' ')
            dateTo.text = ticket.arrivalTime.substringBefore(' ')
            timeFrom.text = ticket.departureTime.substringAfter(' ')
            timeTo.text = ticket.arrivalTime.substringAfter(' ')
            trainNum.text = ticket.trainNum.toString()
            price.text = ticket.price.priceListToString()
            seatsRvAdapter = SeatsRvAdapter(ticket.freeSeats) {
                selectedSeat = it
                setPrice(it, ticket.price, ticket.freeSeats)
            }
            rvAvailableSeats.setHasFixedSize(true)
            rvAvailableSeats.layoutManager = GridLayoutManager(requireContext(), 4)
            rvAvailableSeats.adapter = seatsRvAdapter
        }
    }

    private fun setPrice(seatNum: Int, priceList: List<Double>, freeSeats: List<Int>) {
        val oneThird = freeSeats.size / 3

        price = when {
            seatNum <= oneThird -> priceList[0]
            seatNum <= 2 * oneThird -> priceList[1]
            else -> priceList[2]
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        ticket = null
        seatsRvAdapter = null
    }

}