package com.example.meet_practical.Rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

  // Base URL
  public static final String BASE_URL = "https://randomuser.me/";
  private static Retrofit retrofit = null;


  public static Retrofit getClient() {
    if (retrofit==null) {
      retrofit = new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
    }
    return retrofit;
  }

}