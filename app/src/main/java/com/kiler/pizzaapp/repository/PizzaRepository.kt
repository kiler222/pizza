package com.kiler.pizzaapp.repository

import com.kiler.pizzaapp.api.PizzaApi
import com.kiler.pizzaapp.data.PizzaData
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PizzaRepository @Inject constructor(private val pizzaApi: PizzaApi) {

    fun getPizzaData(): Observable<PizzaData> { //single
        return pizzaApi.getPizzaData()
    }


}