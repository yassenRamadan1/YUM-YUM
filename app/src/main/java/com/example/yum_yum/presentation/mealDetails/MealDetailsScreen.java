package com.example.yum_yum.presentation.mealDetails;

import static com.example.yum_yum.presentation.utils.ImageUtils.loadFlag;

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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.List;


public class MealDetailsScreen extends Fragment implements MealDetailsContract.View {

    private static final String TAG = "MealDetailsScreen";
    private FragmentMealDetailsScreenBinding binding;
    private MealDetailsContract.Presenter presenter;
    private String pendingVideoId = null;
    private boolean isPlayerInitialized = false;

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

        presenter = new MealDetailsPresenter(this);
        postponeEnterTransition();

        if (getArguments() != null) {
            Meal meal = (Meal) getArguments().getSerializable("meal_data");
            presenter.loadMealDetails(meal);
        }

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        setupChipBehavior();
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
        Log.d(TAG, "Video URL: " + videoUrl);
        Log.d(TAG, "Extracted Video ID: " + videoId);
        if (videoId != null && !videoId.isEmpty()) {
            pendingVideoId = videoId;
        } else {
            Log.w(TAG, "Could not extract video ID from URL: " + videoUrl);
            binding.videoCardContainer.setVisibility(View.GONE);
        }
    }

    private void initializeYouTubePlayer(String videoId) {
        if (isPlayerInitialized || binding == null) {
            Log.d(TAG, "Player already initialized or binding is null");
            return;
        }
        getLifecycle().addObserver(binding.youtubePlayerView);
        binding.youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                Log.d(TAG, "YouTube Player is ready!");
                isPlayerInitialized = true;
                binding.videoProgressBar.setVisibility(View.GONE);
                youTubePlayer.cueVideo(videoId, 0);
                Log.d(TAG, "Video cued: " + videoId);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                      @NonNull PlayerConstants.PlayerState state) {
                Log.d(TAG, "Player state: " + state);

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
        isPlayerInitialized = false;
        pendingVideoId = null;
        if (binding != null && binding.youtubePlayerView != null) {
            binding.youtubePlayerView.release();
        }
        binding = null;
    }
}