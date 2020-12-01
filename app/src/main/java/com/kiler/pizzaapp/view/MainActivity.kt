package com.kiler.pizzaapp.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.facebook.*
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
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
import org.json.JSONException


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
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "pizza-data"
    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        sharedPref = applicationContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE)


        setCallbackManager()

        setSubscribtions()

        fabMain!!.setOnClickListener {
            animateFAB()
        }

        fabRecipe!!.setOnClickListener { view ->

            val accessToken = AccessToken.getCurrentAccessToken()
            if (accessToken != null) {
                Log.e(TAG, "token = ${accessToken.token}")
            } else {
                Log.e(TAG, "token = null; trzeba zalogować")
            }


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
            setFacebookLogin()
        }
    }

    private fun setCallbackManager() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.e(TAG, "result @@@onSuccess")
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        Log.e(TAG, "result @@@response: $response")
                        try {
                            val name = `object`.getString("name")
                            val profile_img = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                            val editor = sharedPref!!.edit()
                            editor.putString("fb_username", name)
                            editor.putString("fb_avatar", profile_img)
                            editor.apply()
                            Toast.makeText(applicationContext, getString(R.string.logged_in, name), Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e(TAG, "jsonexcept = ${e.localizedMessage}")
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,picture.width(300).height(300)")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.e(TAG, "result @@@onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.e(TAG, "result @@@onError: " + error.message)
                }
            })
    }


    fun setFacebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this@MainActivity, listOf("public_profile"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
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
        } else {
            fabMain!!.startAnimation(rotate_forward)
            fabFacebookView!!.startAnimation(fab_open)
            fabRecipeView!!.startAnimation(fab_open)
            fabFacebook!!.isClickable = true
            fabRecipe!!.isClickable = true
            isFabOpen = true
            shadowView!!.visibility = View.VISIBLE
//            window.statusBarColor = Color.parseColor("#20111111")
//            window.navigationBarColor = Color.parseColor("#20111111")
        }
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
                        .into(pizzaImgView!!)

                    pizzaImgView.visibility = View.VISIBLE


                }, {
                    Log.e(TAG, "error = ${it.localizedMessage}")
                })
        )
    }



    private fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }


    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }



}