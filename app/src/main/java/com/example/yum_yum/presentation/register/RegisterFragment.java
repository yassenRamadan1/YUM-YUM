package com.example.yum_yum.presentation.register;

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

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnSignup = view.findViewById(R.id.signup_button);
        TextView tvLogin = view.findViewById(R.id.tvLogin);
        MaterialToolbar toolbar = view.findViewById(R.id.empty_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });
        btnSignup.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_homeScreen)
        );
        tvLogin.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginScreen)
        );
    }
}