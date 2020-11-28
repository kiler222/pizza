package com.kiler.pizzaapp.api

import com.kiler.pizzaapp.data.PizzaData
import io.reactivex.Observable
import retrofit2.http.GET


interface PizzaApi {

    @GET("test/info.php")
    fun getPizzaData(): Observable<PizzaData>

}