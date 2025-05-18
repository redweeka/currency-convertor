package com.example.currencyconvertor.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertor.repositories.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    private val TAG = this::class.simpleName
    // For keep it simple, use repository tightly inside view model
    private val currencyRepository = CurrencyRepository

    // Data used live in ui
    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val currencies: StateFlow<List<String>> get() = _currencies

    private val _currencyRatio = MutableStateFlow(1f)
    val currencyRatio: StateFlow<Float> get() = _currencyRatio

    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency: StateFlow<String> = _fromCurrency

    private val _toCurrency = MutableStateFlow("EUR")
    val toCurrency: StateFlow<String> = _toCurrency

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    // First thing (once when started)
    init {
        fetchCurrencies()
    }

    // Get list of currencies available
    private fun fetchCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepository.getCurrenciesList()?.let {
                Log.d(TAG, "fetchCurrencies: ${it.size}")
                _currencies.value = it.sorted()
            }
        }
    }

    // Use when currency pick is changing to update ratio accordingly
    private fun checkCurrencyRatio() {
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepository.getCurrencyRatio(fromCurrency.value, toCurrency.value)?.let {
                _currencyRatio.value = it
            }
        }
    }

    fun switchCurrencies() {
        val previewsFromCurrency = fromCurrency.value
        _fromCurrency.value = toCurrency.value
        _toCurrency.value = previewsFromCurrency
        checkCurrencyRatio()
    }

    ////// Setters //////
    fun setFromCurrency(value: String) {
        _fromCurrency.value = value
        checkCurrencyRatio()
    }

    fun setToCurrency(value: String) {
        _toCurrency.value = value
        checkCurrencyRatio()
    }

    fun setAmount(value: String) {
        _amount.value = value
    }
}