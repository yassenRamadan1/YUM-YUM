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
import com.example.yum_yum.databinding.FragmentLoginBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;


public class LoginScreen extends Fragment implements LoginContract.View {

    private LoginContract.Presenter presenter;
    private FragmentLoginBinding _binding;

    public LoginScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentLoginBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new LoginPresenterImpl(this, this.requireContext().getApplicationContext());

        _binding.emptyToolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });

        _binding.loginButton.setOnClickListener(v -> {
                    String email = _binding.emailEditText.getText().toString().trim();
                    String password = _binding.passwordEditText.getText().toString().trim();
                    presenter.login(email, password);
                }
        );

        _binding.tvSignUp.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginScreen_to_registerFragment)
        );
    }

    @Override
    public void showLoading() {
        _binding.loadingIndicator.setVisibility(View.VISIBLE);
        _binding.loginButton.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        _binding.loadingIndicator.setVisibility(View.GONE);
        _binding.loginButton.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setEmailError(String error) {
        _binding.emailEdittextLayout.setError(error);
    }

    @Override
    public void setPasswordError(String error) {
        _binding.passwordInputLayout.setError(error);
    }

    @Override
    public void navigateToHome() {
        Navigation.findNavController(requireView()).navigate(R.id.action_global_homeScreen);
    }
}