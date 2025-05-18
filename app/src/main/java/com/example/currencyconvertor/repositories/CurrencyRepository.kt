package com.example.currencyconvertor.repositories

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object CurrencyRepository {
    private val TAG = this::class.simpleName

    // Get list of currencies
    fun getCurrenciesList(): List<String>? {
        var currencies: List<String>? = null
        // Use random currency (USD)
        val url = "https://open.er-api.com/v6/latest/USD"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    currencies = responseBody?.let {
                        val jsonObject = Gson().fromJson(it, JsonObject::class.java)

                        // Take all currencies from rates list
                        val ratesObject = jsonObject?.getAsJsonObject("rates")
                        ratesObject?.keySet()?.toList()
                    }
                } else {
                    Log.e(TAG, "getCurrenciesList: Failed to retrieve data. Status code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "getCurrenciesList: Network error: ", e)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "getCurrenciesList: JSON parsing error: ", e)
        }

        Log.d(TAG, "getCurrenciesList: $currencies")
        return currencies
    }

    // Get currency complete data
    fun getCurrencyRatio(fromCurrencyId: String, toCurrencyId: String): Float? {
        var currencyRatio: Float? = null
        val url = "https://open.er-api.com/v6/latest/$fromCurrencyId"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    currencyRatio = responseBody?.let {
                        val jsonObject = Gson().fromJson(it, JsonObject::class.java)
                        val rates = jsonObject?.getAsJsonObject("rates")
                        rates?.get(toCurrencyId)?.asFloat
                    }
                } else {
                    Log.e(TAG, "getCurrencyData: Failed to retrieve data. Status code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "getCurrencyData: Network error: ", e)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "getCurrencyData: JSON parsing error: ", e)
        }

        return currencyRatio
    }
}
