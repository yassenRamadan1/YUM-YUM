package com.example.yum_yum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private boolean isKeepOn = true; // Flag to keep the splash screen visible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Install the Splash Screen (MUST be before setContentView)
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Configure the Splash Screen to wait for data
        splashScreen.setKeepOnScreenCondition(() -> isKeepOn);

        // 3. Run your RxJava Logic (Check Auth / Database)
//        checkUserStatus();
    }
//
//    private void checkUserStatus() {
//        // Simulating a check (e.g., Firebase or SharedPreferences)
//        // using RxJava as required
//        Observable.timer(2, TimeUnit.SECONDS) // Wait 2 seconds (or replace with real network call)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aLong -> {
//
//                    // A. Check if user is logged in (Example logic)
//                    SharedPreferences prefs = getSharedPreferences("FoodPlannerPrefs", MODE_PRIVATE);
//                    boolean isGuest = prefs.getBoolean("is_guest", false);
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                    if (user == null && !isGuest) {
//                        // User needs to login -> Go to AuthActivity
//                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
//                        startActivity(intent);
//                        finish(); // Close MainActivity so they can't go back
//                    }
//
//                    // If user IS logged in, we do nothing.
//                    // The splash disappears, and MainActivity reveals itself.
//
//                    // 4. Release the Splash Screen
//                    isKeepOn = false;
//                });
//    }
}