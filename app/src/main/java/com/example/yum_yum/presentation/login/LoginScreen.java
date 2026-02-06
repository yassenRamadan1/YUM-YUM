package com.example.yum_yum.presentation.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentLoginBinding;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.snackbar.Snackbar;


public class LoginScreen extends Fragment implements LoginContract.View {

    private LoginContract.Presenter presenter;
    private FragmentLoginBinding _binding;
    private CredentialManager credentialManager;

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

        credentialManager = CredentialManager.create(requireContext());

        _binding.loginUsingGoogleButton.setOnClickListener(v -> {
            presenter.onGoogleSignInClicked();
        });
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
    public void showGoogleSignInError(String message) {
        showError(message);
    }

    @Override
    public void launchGoogleSignIn(String webClientId) {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                null,
                requireContext().getMainExecutor(),
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, androidx.credentials.exceptions.GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        if (result.getCredential() instanceof GoogleIdTokenCredential) {
                            GoogleIdTokenCredential credential = (GoogleIdTokenCredential) result.getCredential();
                            String idToken = credential.getIdToken();
                            presenter.onGoogleSignInSuccess(idToken);
                        } else {
                            presenter.onGoogleSignInFailure(new Exception("Unexpected credential type"));
                        }
                    }

                    @Override
                    public void onError(androidx.credentials.exceptions.GetCredentialException e) {
                        presenter.onGoogleSignInFailure(e);
                    }
                }
        );
    }
        @Override
        public void navigateToHome () {
            Navigation.findNavController(requireView()).navigate(R.id.action_global_homeScreen);
        }
    }