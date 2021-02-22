package com.raghu.viitorclouddemo.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient mInstance;
    private static final String BASE_URL ="https://api.npoint.io";
    private Retrofit retrofit;

    private RetrofitClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5,TimeUnit.MINUTES)
                .readTimeout(5,TimeUnit.MINUTES)
                .build();

        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if (mInstance == null){
            mInstance =new RetrofitClient();
        }
        return mInstance;
    }
    public RequestApi getApi(){
        return retrofit.create(RequestApi.class);
    }
}
