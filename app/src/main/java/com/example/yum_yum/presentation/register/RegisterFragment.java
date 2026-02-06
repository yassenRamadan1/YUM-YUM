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
import com.example.yum_yum.databinding.FragmentRegisterBinding;
import com.example.yum_yum.presentation.login.LoginContract;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class RegisterFragment extends Fragment implements RegisterContract.View {

    private RegisterContract.Presenter presenter;
    private FragmentRegisterBinding _binding;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new RegisterPresenterImpl(this,this.requireContext().getApplicationContext());

        _binding.emptyToolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });
        _binding.signupButton.setOnClickListener(v -> {
                    String user = _binding.usernameEditText.getText().toString().trim();
                    String mail = _binding.emailEditText.getText().toString().trim();
                    String pass = _binding.passwordEditText.getText().toString().trim();
                    presenter.register(user, mail, pass);
                }
        );
        _binding.tvLogin.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginScreen)
        );
    }

    @Override
    public void showLoading() {
        _binding.loadingIndicator.setVisibility(View.VISIBLE);
        _binding.signupButton.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        _binding.loadingIndicator.setVisibility(View.GONE);
        _binding.signupButton.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setEmailError(String error) {
        _binding.emailInputLayout.setError(error);

    }

    @Override
    public void setPasswordError(String error) {
        _binding.passwordEditText.setError(error);
    }

    @Override
    public void setUsernameError(String error) {
        _binding.usernameInputLayout.setError(error);
    }

    @Override
    public void navigateToHome() {
        Navigation.findNavController(requireView()).navigate(R.id.action_global_homeScreen);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        _binding = null;
    }
}