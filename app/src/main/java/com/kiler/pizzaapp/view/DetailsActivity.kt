package com.kiler.pizzaapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kiler.pizzaapp.R
import com.kiler.pizzaapp.data.PizzaData
import com.kiler.pizzaapp.utils.saveImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val PRIVATE_MODE = 0
        val PREF_NAME = "pizza-data"
        val bundle = intent.getBundleExtra("pizzaData")

        val pizzaData = bundle.getParcelable<PizzaData>("pizzaData") ?: PizzaData("",
            "",
            Array(0) { "" },
            Array(
                0
            ) { "" },
            Array(0) { "" })

        val pizzaBody = findViewById<TextView>(R.id.pizzaBody)
        val loggedUser = findViewById<TextView>(R.id.loggedUser)
        val ingredientsBody = findViewById<TextView>(R.id.ingredientsBody)
        val preparingBody = findViewById<TextView>(R.id.preparingBody)
//        val pizzaImgLeft = findViewById<ImageView>(R.id.pizzaImgLeft)
//        val pizzaImgRight = findViewById<ImageView>(R.id.pizzaImgRight)

        var ingredientsText = ""
        var preparingText = ""
        val sharedPref = applicationContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val username = sharedPref!!.getString("fb_username", "Anonymouse")
        val avatarUrl = sharedPref.getString("fb_avatar", "no_avatar")


        loggedUser.text = getString(R.string.logged_as, username)

        if (avatarUrl != "no_avatar") setAvatarImg(avatarUrl!!)

        setPizzaImages(pizzaData.imgs[1], pizzaData.imgs[2])

        pizzaBody.text = pizzaData.description

        pizzaData.ingredients.forEach { ingredientsText += "- $it\n" }

        ingredientsBody.text = ingredientsText

        for (i in 0..pizzaData.preparing.size - 1) {
            preparingText += "${i+1}. ${pizzaData.preparing[i]}\n\n"
        }

        preparingBody.text = preparingText


    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun setPizzaImages(imgLeft: String, imgRight: String) {

        val pizzaImgLeft = findViewById<ImageView>(R.id.pizzaImgLeft)
        val pizzaImgRight = findViewById<ImageView>(R.id.pizzaImgRight)

        val requestOption = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.IMMEDIATE)
            .dontAnimate()

        Glide.with(this)
            .applyDefaultRequestOptions(requestOption)
            .load(imgLeft)
            .into(pizzaImgLeft)

        Glide.with(this)
            .applyDefaultRequestOptions(requestOption)
            .load(imgRight)
            .into(pizzaImgRight)


        pizzaImgLeft.setOnClickListener{

            if (checkPermisions()) {
                CoroutineScope(Dispatchers.IO).launch {

                    val image = Glide.with(this@DetailsActivity)
                        .asBitmap()
                        .load(imgLeft)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error)
                        .submit()
                        .get()

                    image.saveImage(applicationContext)
                }
            }
        }

        pizzaImgRight.setOnClickListener{

            if (checkPermisions()) {
                CoroutineScope(Dispatchers.IO).launch {

                    val image = Glide.with(this@DetailsActivity)
                        .asBitmap()
                        .load(imgRight)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error)
                        .submit()
                        .get()

                    image.saveImage(applicationContext)
                }
            }
        }


    }


    private fun checkPermisions(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                200
            )
            false
        } else {

            true
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) checkPermisions()
    }


    private fun setAvatarImg(avatarUrl: String) {

        val avatarImg = findViewById<ImageView>(R.id.avatarImg)

        val requestOption = RequestOptions()
            .override(100, 100)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.IMMEDIATE)
            .dontAnimate()

        Glide.with(this)
            .applyDefaultRequestOptions(requestOption)
            .load(avatarUrl)
            .into(avatarImg)
    }




}