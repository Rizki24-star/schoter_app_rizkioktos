package com.example.schoter_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoter_app.adapter.RecyclerAdapter
import com.example.schoter_app.api.newsApi
import com.example.schoter_app.model.NewsApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import kotlin.math.log
import kotlin.system.measureTimeMillis

const val BASE_URL = "https://newsapi.org"

class MainActivity : AppCompatActivity() {

    lateinit var countDownTimer: CountDownTimer

    private var titleList = mutableListOf<String>()
    private var publishList = mutableListOf<String>()
    private var desclist = mutableListOf<String>()
    private var imageList = mutableListOf<String>()
    private var linkList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeApiRequest()

    }

    private fun fadeInFromBlack(){
        blackscreen.animate().apply {
            alpha(0f)
            duration = 3000
        }.start()
    }

    private fun setUpRecyclerView(){
        rv_recylerView.layoutManager = LinearLayoutManager(applicationContext)
        rv_recylerView.adapter = RecyclerAdapter(titleList,publishList,desclist,imageList,linkList)
    }

    private fun addToList(title: String, publish: String, description: String, image: String, link: String){
        titleList.add(title)
        publishList.add(publish)
        desclist.add(description)
        imageList.add(image)
        linkList.add(link)
    }

    private fun makeApiRequest(){

        progressBar.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(newsApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response: NewsApi = api.getNews()

                for (article in response.articles){
                    Log.i("MainActivity","Result = $article")
                    addToList(article.title, article.publishedAt ,article.description, article.urlToImage, article.url)
                }

                withContext(Dispatchers.Main){
                    setUpRecyclerView()
                    fadeInFromBlack()
                    progressBar.visibility = View.GONE
                }

            }catch (e:Exception){
                Log.e("MainActivity", e.toString())

                withContext(Dispatchers.Main){
                    attemptRequestAgain()
                }
            }
        }
    }

    private fun attemptRequestAgain(){
        countDownTimer = object : CountDownTimer(5*1000,1000){
            override fun onTick(p0: Long) {
               Log.i("MainActivity","Tidak dapat menjangkau serer... Coba lagi nanti dalam ${p0/1000} detik")
            }

            override fun onFinish() {
               makeApiRequest()
                countDownTimer.cancel()
            }

        }

        countDownTimer.start()
    }
}

