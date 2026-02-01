package com.example.yum_yum.presentation.login;

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
import com.google.android.material.appbar.MaterialToolbar;


public class LoginScreen extends Fragment {

        public LoginScreen() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_login, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Button btnLogin = view.findViewById(R.id.login_button);
            TextView tvSignUp = view.findViewById(R.id.tvSignUp);
            MaterialToolbar toolbar = view.findViewById(R.id.empty_toolbar);
            toolbar.setNavigationOnClickListener(v -> {
                Navigation.findNavController(view).navigateUp();
            });

            btnLogin.setOnClickListener(v ->
                    Navigation.findNavController(view).navigate(R.id.action_loginScreen_to_homeScreen)
            );

            tvSignUp.setOnClickListener(v ->
                    Navigation.findNavController(view).navigate(R.id.action_loginScreen_to_registerFragment)
            );
        }
    }