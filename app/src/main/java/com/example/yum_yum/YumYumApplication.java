package com.example.yum_yum;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class YumYumApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupRxJavaErrorHandler();
    }

    private void setupRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException) {
                throwable = throwable.getCause();
            }

            if (throwable instanceof IOException || throwable instanceof UnknownHostException) {
                Log.w("RxJava", "Network error (ignored): " + throwable.getMessage());
                return;
            }
            if (throwable instanceof InterruptedException) {
                Log.w("RxJava", "Interrupted (ignored): " + throwable.getMessage());
                return;
            }
            if (throwable instanceof NullPointerException || throwable instanceof IllegalArgumentException) {
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), throwable);
                return;
            }
            if (throwable instanceof IllegalStateException) {
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), throwable);
                return;
            }

            Log.w("RxJava", "Undeliverable exception (ignored): " + throwable.getMessage());
        });
    }
}
