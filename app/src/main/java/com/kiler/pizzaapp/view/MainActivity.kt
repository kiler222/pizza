package com.kiler.pizzaapp.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.facebook.CallbackManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kiler.pizzaapp.R
import com.kiler.pizzaapp.data.PizzaData
import com.kiler.pizzaapp.model.PizzaViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


@AndroidEntryPoint
class MainActivity  : AppCompatActivity() {

    private lateinit var pizzaViewModel: PizzaViewModel
    private var pizzaData: PizzaData? = null
    private val subscriptions = CompositeDisposable()
    private val TAG = "PJmainAct"
    private lateinit var callbackManager: CallbackManager

    private var isFabOpen = false
    private var fabMain: FloatingActionButton? = null
    private  var fabFacebook:FloatingActionButton? = null
    private  var fabRecipe:FloatingActionButton? = null
    private  var fabFacebookView: LinearLayout? = null
    private  var fabRecipeView: LinearLayout? = null
    private var shadowView: View? = null
    private var fab_open: Animation? = null
    private  var fab_close:Animation? = null
    private  var rotate_forward:Animation? = null
    private  var rotate_backward:Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

        pizzaViewModel = ViewModelProvider(this).get(PizzaViewModel::class.java)

        fabFacebookView = findViewById<LinearLayout>(R.id.fabFacebookView)
        fabRecipeView = findViewById<LinearLayout>(R.id.fabRecipeView)
        fabMain = findViewById(R.id.fabMain)
        fabFacebook = findViewById(R.id.fabFacebook)
        fabRecipe = findViewById(R.id.fabRecipe)
        shadowView = findViewById(R.id.shadowView)
        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        rotate_forward = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.rotate_forward
        )
        rotate_backward = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.rotate_backward
        )


        setSubscribtions()

        fabMain!!.setOnClickListener {
            animateFAB()
        }


        fabRecipe!!.setOnClickListener { view ->


            if (pizzaData != null && pizzaData!!.title != "") {

                val intent = Intent(this, DetailsActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("pizzaData", pizzaData)
                intent.putExtra("pizzaData", bundle)
                startActivity(intent)

            } else {

                Snackbar.make(view, getString(R.string.pizza_not_ready), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()

            }

        }

        fabFacebook!!.setOnClickListener {


            callbackManager = CallbackManager.Factory.create()


        }






    }


    fun animateFAB() {
        if (isFabOpen) {
            fabMain!!.startAnimation(rotate_backward)
            fabFacebookView!!.startAnimation(fab_close)
            fabRecipeView!!.startAnimation(fab_close)
            fabFacebook!!.isClickable = false
            fabRecipe!!.isClickable = false
            isFabOpen = false
            shadowView!!.visibility = View.INVISIBLE
            Log.d("PJ", "close")
        } else {
            fabMain!!.startAnimation(rotate_forward)
            fabFacebookView!!.startAnimation(fab_open)
            fabRecipeView!!.startAnimation(fab_open)
//            fabFacebookView!!.visibility = View.VISIBLE
//            fabRecipeView!!.visibility = View.VISIBLE
            fabFacebook!!.isClickable = true
            fabRecipe!!.isClickable = true
            isFabOpen = true
            shadowView!!.visibility = View.VISIBLE
//            window.statusBarColor = Color.parseColor("#20111111")
//            window.navigationBarColor = Color.parseColor("#20111111")
            Log.d("PJ", "open")
        }
    }


    override fun onResume() {
        super.onResume()
//        animateFAB()
    }

    private fun setSubscribtions() {
        subscribe(
            pizzaViewModel.getPizzaData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pizzaData = it

                    val url = pizzaData!!.imgs[0]
                    val pizzaImgView = findViewById<ImageView>(R.id.pizza_image)

                    val requestOption = RequestOptions()
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .override(200, 200)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE)
                        .dontAnimate()

                    Glide.with(this)
                        .applyDefaultRequestOptions(requestOption)
                        .load(url)
//                        .thumbnail(0.5f)
                        .into(pizzaImgView!!)

                    pizzaImgView.visibility = View.VISIBLE




                }, {
                    Log.e(TAG, "error = ${it.localizedMessage}")
                })
        )
    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }



}