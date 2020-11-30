package com.kiler.pizzaapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.kiler.pizzaapp.R
import com.kiler.pizzaapp.data.PizzaData
import dagger.hilt.android.AndroidEntryPoint


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val bundle = intent.getBundleExtra("pizzaData")

        val pizzaData = bundle.getParcelable<PizzaData>("pizzaData") ?: PizzaData("", "", Array(0) {""}, Array(0) {""}, Array(0) {""})

        val pizzaBody = findViewById<TextView>(R.id.pizzaBody)
        val ingredientsBody = findViewById<TextView>(R.id.ingredientsBody)
        val preparingBody = findViewById<TextView>(R.id.preparingBody)
        var ingredientsText = ""
        var preparingText = ""

        pizzaBody.text = pizzaData.description


        pizzaData.ingredients.forEach { ingredientsText += "- $it\n" }
        ingredientsBody.text = ingredientsText


        for (i in 0..pizzaData.preparing.size - 1) {
            preparingText += "${i+1}. ${pizzaData.preparing[i]}\n\n"
        }

        preparingBody.text = preparingText


    }
}