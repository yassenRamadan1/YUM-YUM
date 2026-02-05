package com.example.yum_yum.data.network;

import de.hdodenhof.circleimageview.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private String baseUrl = "https://www.themealdb.com/api/json/v1/1/";
    private static volatile Retrofit retrofit;

    private NetworkClient(){
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(logging);
        }

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }
    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (NetworkClient.class) {
                if (retrofit == null) {
                    new NetworkClient();
                }
            }
        }
        return retrofit;
    }
}
