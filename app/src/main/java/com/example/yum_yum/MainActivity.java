package com.example.yum_yum;

import static java.lang.Thread.sleep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private boolean isKeepOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        splashScreen.setOnExitAnimationListener(splashScreenViewProvider -> {
            final View splashScreenView = splashScreenViewProvider.getView();
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.getHeight()
            );
            slideUp.setInterpolator(new AnticipateInterpolator());
            slideUp.setDuration(500L);

            slideUp.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    splashScreenViewProvider.remove();
                }
            });
            slideUp.start();
        });

        BottomNavigationView bottomNav = findViewById(R.id.nav_bar);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.welcomeScreen || id == R.id.loginScreen) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 2. Configure the Splash Screen to wait for data
//        splashScreen.setKeepOnScreenCondition(() -> isKeepOn);

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