package com.example.yum_yum.presentation.profile;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.yum_yum.R;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter presenter;
    private TextView nameTextView;
    private View loadingIndicator;
    private Button logoutButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProfilePresenterImpl(this, requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // You'll need to create this layout file (fragment_profile.xml)
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.profile_name_text);
        logoutButton = view.findViewById(R.id.logout_button);
        loadingIndicator = view.findViewById(R.id.loading_indicator_profile);

        logoutButton.setOnClickListener(v -> presenter.logout());

        presenter.loadUserProfile();

        view.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
        logoutButton.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        loadingIndicator.setVisibility(View.GONE);
        logoutButton.setEnabled(true);
    }

    @Override
    public void showUserName(String name) {
        nameTextView.setText(name);
    }

    @Override
    public void navigateToWelcome() {
        Navigation.findNavController(requireView()).navigate(R.id.action_global_welcomeScreen);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}