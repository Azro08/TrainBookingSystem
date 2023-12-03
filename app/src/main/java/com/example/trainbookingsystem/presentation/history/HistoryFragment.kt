package com.example.trainbookingsystem.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.databinding.FragmentHistoryBinding
import com.example.trainbookingsystem.util.Constants
import com.example.trainbookingsystem.util.ScreenState
import com.example.trainbookingsystem.util.UsersManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()

    @Inject
    lateinit var usersManager: UsersManager
    private var historyRvAdapter: HistoryRvAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (usersManager.getRole() == Constants.USER) getUsersHistory()
        else getAllUsersHistory()

        binding.swipeToRefreshLayout.setOnRefreshListener {
            viewModel.refresh(usersManager.getRole())
            binding.swipeToRefreshLayout.isRefreshing = false
        }

    }

    private fun getAllUsersHistory() {
        lifecycleScope.launch {
            viewModel.getAllHistory()
            viewModel.history.collect { state ->
                processResponse(state)
            }
        }
    }

    private fun processResponse(state: ScreenState<List<TicketCheck>?>) {
        when (state) {

            is ScreenState.Loading -> {
                binding.loadingGig.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
                binding.textViewError.visibility = View.GONE
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    binding.loadingGig.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                    displayData(state.data)
                } else handleError(getString(R.string.error))
            }

            is ScreenState.Error -> {
                handleError(state.message.toString())
            }

        }
    }

    private fun displayData(checkList: List<TicketCheck>) {
        historyRvAdapter = HistoryRvAdapter(checkList)
        binding.rvHistory.setHasFixedSize(true)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = historyRvAdapter
    }

    private fun handleError(msg: String) {
        binding.loadingGig.visibility = View.GONE
        binding.rvHistory.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = msg
    }

    private fun getUsersHistory() {
        lifecycleScope.launch {
            viewModel.getUsersHistory()
            viewModel.history.collect { state ->
                processResponse(state)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        historyRvAdapter = null
    }

}