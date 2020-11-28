package com.kiler.pizzaapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PizzaData(
    val title: String,
    val description: String,
    val ingredients: Array<String>,
    val preparing: Array<String>,
    val imgs: Array<String>

): Parcelable
