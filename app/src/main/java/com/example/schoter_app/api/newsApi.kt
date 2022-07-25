package com.example.schoter_app.api

import com.example.schoter_app.model.NewsApi
import retrofit2.http.GET

interface newsApi {

    @GET("/v2/top-headlines?sources=techcrunch&apiKey=19fb196679d142f1bc1054d999dbf046")
    suspend fun getNews(): NewsApi
}