package com.kiler.pizzaapp.repository


import com.google.common.truth.Truth.assertThat
import com.kiler.pizzaapp.api.PizzaApi
import com.kiler.pizzaapp.data.PizzaData
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Before
import org.junit.Test



class PizzaRepositoryTest {


    lateinit var pizzaApi: PizzaApi
    lateinit var pizzaRepository: PizzaRepository
    var sPizzaData: Single<PizzaData>? = null


    @Before
    fun setUp() {
        pizzaApi = mock()
        pizzaRepository = PizzaRepository(pizzaApi)
    }


    @Test
    fun getPizzaData_return_type_is_single_pizzadata() {

        val result = pizzaRepository.getPizzaData()
        assertThat(result).isSameInstanceAs(sPizzaData)

    }


}