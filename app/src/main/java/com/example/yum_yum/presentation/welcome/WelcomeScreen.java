package com.example.yum_yum.presentation.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.yum_yum.R;

public class WelcomeScreen extends Fragment {


    public WelcomeScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnSignup = view.findViewById(R.id.signUp);
        TextView tvGuest = view.findViewById(R.id.tvEnterAsGuest);

        btnLogin.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeScreen_to_loginScreen)
        );
        btnSignup.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeScreen_to_registerFragment)
        );
        tvGuest.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeScreen_to_homeScreen)
        );
    }
}

