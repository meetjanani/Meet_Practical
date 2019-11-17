package com.example.meet_practical.Rest;

import com.example.meet_practical.UserList_Bean.Response_Main;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    // End Point TO Get Random User
    // results param Record Size Count
    @GET("api/")
    Call<Response_Main> Login_API( @Query("results") int results);

}