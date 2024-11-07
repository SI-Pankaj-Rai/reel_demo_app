package com.rai.reelsdemo.data.remote

import org.json.JSONObject
import retrofit2.http.GET

interface HomeApiService {
    @GET
    suspend fun getHomeData():JSONObject
}