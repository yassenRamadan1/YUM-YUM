package com.example.yum_yum.presentation.mealDetails;

import static com.example.yum_yum.presentation.utils.ImageUtils.loadFlag;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentMealDetailsScreenBinding;
import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.utils.FlagManger;
import com.example.yum_yum.presentation.utils.YouTubeUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MealDetailsScreen extends Fragment implements MealDetailsContract.View {

    private static final String TAG = "MealDetailsScreen";
    private FragmentMealDetailsScreenBinding binding;
    private MealDetailsContract.Presenter presenter;
    private String pendingVideoId = null;
    private boolean isPlayerInitialized = false;
    private Meal currentMeal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMealDetailsScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new MealDetailsPresenter(this, requireContext());
        postponeEnterTransition();

        if (getArguments() != null) {
            currentMeal = (Meal) getArguments().getSerializable("meal_data");
            presenter.loadMealDetails(currentMeal);
        }

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        setupChipBehavior();
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.iconFavorite.setOnClickListener(v -> {
            if (currentMeal != null) {
                presenter.onFavoriteClicked(currentMeal);
            }
        });
        binding.iconCalendar.setOnClickListener(v -> {
            if (currentMeal != null) {
                presenter.onCalendarClicked(currentMeal);
            }
        });
    }

    private void setupChipBehavior() {
        binding.chipsList.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            boolean showInstructions = (id == binding.chipInstructions.getId());
            boolean showIngredients = (id == binding.chipIngredient.getId());
            boolean showVideo = (id == binding.chipVideo.getId());
            binding.textInstructions.setVisibility(showInstructions ? View.VISIBLE : View.GONE);
            binding.recyclerIngredients.setVisibility(showIngredients ? View.VISIBLE : View.GONE);
            binding.videoCardContainer.setVisibility(showVideo ? View.VISIBLE : View.GONE);

            if (showVideo && !isPlayerInitialized && pendingVideoId != null) {
                initializeYouTubePlayer(pendingVideoId);
            }
        });
    }

    @Override
    public void showMealInfo(Meal meal) {
        if (binding == null) return;
        binding.textDishName.setText(meal.getName());
        binding.textCountry.setText(meal.getArea());
        binding.textCategory.setText(meal.getCategory());
        binding.textInstructions.setText(meal.getInstructions());

        FlagManger flagManger = FlagManger.getInstance();
        String flagUrl = flagManger.getFlagUrl(meal.getArea());
        loadFlag(binding.flagItemIcon, flagUrl);

        Glide.with(this)
                .load(meal.getImageUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(binding.imageBackground);
    }

    @Override
    public void showIngredientsList(List<IngredientItem> ingredients) {
        if (binding == null) return;
        IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
        binding.recyclerIngredients.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recyclerIngredients.setAdapter(adapter);
    }

    @Override
    public void setupVideo(String videoUrl) {
        if (binding == null || videoUrl == null || videoUrl.isEmpty()) {
            return;
        }
        String videoId = YouTubeUtils.extractVideoId(videoUrl);
        if (videoId != null && !videoId.isEmpty()) {
            pendingVideoId = videoId;
        } else {
            binding.videoCardContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoginRequired(String feature) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("You must be logged in to add meals to " + feature + ".")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
        if (binding == null) return;
        binding.iconFavorite.setImageResource(
                isFavorite ? R.drawable.ic_love_icon : R.drawable.ic_love_icon_white
        );
    }

    @Override
    public void showCalendarPicker(Meal meal) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayInMillis = today.getTimeInMillis();
        Calendar endOfWeek = (Calendar) today.clone();
        int currentDayOfWeek = endOfWeek.get(Calendar.DAY_OF_WEEK);
        int daysUntilFriday = (Calendar.FRIDAY - currentDayOfWeek + 7) % 7;

        if (daysUntilFriday == 0) {
            daysUntilFriday = 7;
        }
        endOfWeek.add(Calendar.DAY_OF_YEAR, daysUntilFriday);
        endOfWeek.set(Calendar.HOUR_OF_DAY, 23);
        endOfWeek.set(Calendar.MINUTE, 59);
        endOfWeek.set(Calendar.SECOND, 59);
        long endOfWeekInMillis = endOfWeek.getTimeInMillis();

        Log.d(TAG, "Today: " + formatDate(todayInMillis));
        Log.d(TAG, "End of cycle (Friday): " + formatDate(endOfWeekInMillis));
        CalendarConstraints.DateValidator validatorForward =
                DateValidatorPointForward.from(todayInMillis);
        CalendarConstraints.DateValidator validatorBackward =
                DateValidatorPointBackward.before(endOfWeekInMillis);
        CalendarConstraints.DateValidator compositeValidator =
                CompositeDateValidator.allOf(
                        Arrays.asList(validatorForward, validatorBackward)
                );

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setStart(todayInMillis)
                .setEnd(endOfWeekInMillis)
                .setOpenAt(todayInMillis)
                .setValidator(compositeValidator)
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date (Sat - Fri)")
                .setSelection(todayInMillis)
                .setCalendarConstraints(constraints)
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String selectedDate = formatDate(selection);
            Log.d(TAG, "Selected date: " + selectedDate);
            presenter.onDateSelected(meal, selectedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private void initializeYouTubePlayer(String videoId) {
        if (isPlayerInitialized || binding == null) {
            return;
        }
        getLifecycle().addObserver(binding.youtubePlayerView);
        binding.youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                isPlayerInitialized = true;
                binding.videoProgressBar.setVisibility(View.GONE);
                youTubePlayer.cueVideo(videoId, 0);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                      @NonNull PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.PLAYING ||
                        state == PlayerConstants.PlayerState.PAUSED ||
                        state == PlayerConstants.PlayerState.VIDEO_CUED) {
                    binding.videoProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer,
                                @NonNull PlayerConstants.PlayerError error) {
                binding.videoProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        isPlayerInitialized = false;
        pendingVideoId = null;
        if (binding != null && binding.youtubePlayerView != null) {
            binding.youtubePlayerView.release();
        }
        binding = null;
    }
}