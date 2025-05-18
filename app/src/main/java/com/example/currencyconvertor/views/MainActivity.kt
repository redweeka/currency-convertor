package com.example.currencyconvertor.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconvertor.ui.theme.CurrencyConvertorTheme
import com.example.currencyconvertor.viewModels.CurrencyViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CurrencyConvertorTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Init view model
                    val currencyViewModel: CurrencyViewModel = viewModel()
                    CurrencyConverterScreen(currencyViewModel)
                }
            }
        }
    }
}

@Composable
fun CurrencyConverterScreen(currencyViewModel: CurrencyViewModel) {
    // Use data live from view model
    val currencies by currencyViewModel.currencies.collectAsState()
    val ratio by currencyViewModel.currencyRatio.collectAsState()
    val amount by currencyViewModel.amount.collectAsState()
    val fromCurrency by currencyViewModel.fromCurrency.collectAsState()
    val toCurrency by currencyViewModel.toCurrency.collectAsState()

    // Live update result when amount or ratio changing
    val result = remember(amount, ratio) {
        val newResult = try {
            amount.toFloat() * ratio
        } catch (e: NumberFormatException) {
            ratio
        }

        newResult
    }

    // Get date once when entering this screen
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val lastUpdated = now.format(formatter)

    // Align all screen vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // From Currency Dropdown
        CurrencyDropdown("From:", currencies, fromCurrency) { currencyViewModel.setFromCurrency(it) }

        // Amount Input
        OutlinedTextField(
            value = amount,
            onValueChange = { amountText -> currencyViewModel.setAmount(amountText) },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Switch Button
        IconButton(
            onClick = { currencyViewModel.switchCurrencies() }
        ) {
            Icon(
                imageVector = Icons.Filled.SwapVert,
                contentDescription = "Switch currencies"
            )
        }

        // To Currency Dropdown
        CurrencyDropdown("To:", currencies, toCurrency) { currencyViewModel.setToCurrency(it) }

        // Result Output
        Text(
            // Show two numbers after the dot
            text = "Result: ${Currency.getInstance(toCurrency).symbol}${String.format("%.2f", result)}",
            style = MaterialTheme.typography.bodyLarge
        )

        // Currency Ratio Output
        Text(
            // Show two numbers after the dot
            text = "1 $fromCurrency = ${String.format("%.2f", ratio)} $toCurrency",
            style = MaterialTheme.typography.bodyMedium
        )

        // Last Updated Output
        Text(
            text = "Last Updated: $lastUpdated",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun CurrencyDropdown(label: String, options: List<String>, selected: String, onSelectedChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    // Align dropdown vertically
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label above dropdown
        Text(text = label, style = MaterialTheme.typography.labelLarge)

        // Dropdown text
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Open dropdown")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        // Make sure options are not empty
        if (options.isNotEmpty()) {
            // Currencies options layout
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectedChange(option)
                            expanded = false
                        }
                    )
                }
            }
        } else {
            // Error note
            Text(
                text = "No currencies available",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Used when coding to preview changes fast
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CurrencyConvertorTheme {
        val currencyViewModel = CurrencyViewModel()

        CurrencyConverterScreen(currencyViewModel)
    }
}