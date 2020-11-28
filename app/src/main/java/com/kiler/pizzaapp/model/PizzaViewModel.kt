package com.kiler.pizzaapp.model

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.kiler.pizzaapp.data.PizzaData
import com.kiler.pizzaapp.repository.PizzaRepository
import io.reactivex.Observable

class PizzaViewModel @ViewModelInject constructor(private val repository: PizzaRepository) : ViewModel() {

    private val TAG = "PJviewModel"

    fun getPizzaData(): Observable<PizzaData> {
        return repository.getPizzaData()
            .map {
                it
            }
            .onErrorReturn {
                Log.e(TAG,"An error occurred $it")
                PizzaData("","", Array(0) {""},
                    Array(0) {""}, Array(0) {""})
            }
    }

}