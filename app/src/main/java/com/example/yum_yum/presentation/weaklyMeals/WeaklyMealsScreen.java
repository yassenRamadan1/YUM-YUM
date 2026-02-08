package com.example.yum_yum.presentation.weaklyMeals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yum_yum.databinding.FragmentWeaklyMealsBinding;
import com.example.yum_yum.presentation.model.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeaklyMealsScreen extends Fragment implements WeeklyMealsContract.View {
    private FragmentWeaklyMealsBinding binding;
    private WeeklyMealsContract.Presenter presenter;
    private CalendarDayAdapter calendarAdapter;
    private PlannedMealAdapter mealAdapter;
    private Map<String, List<Meal>> mealsByDate;
    private String selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeaklyMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new WeeklyMealsPresenter(this, requireContext());
        setupCalendar();
        setupMealsRecycler();
        presenter.loadWeeklyMeals();
    }

    private void setupCalendar() {
        List<CalendarDayAdapter.CalendarDay> days = generateWeekDays();

        calendarAdapter = new CalendarDayAdapter(days, (day, position) -> {
            calendarAdapter.setSelectedPosition(position);
            selectedDate = day.getDate();
            updateSelectedDateLabel(day);
            updateMealsForSelectedDate();
        });

        binding.calendarRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.calendarRecycler.setAdapter(calendarAdapter);
        for (CalendarDayAdapter.CalendarDay day : days) {
            if (day.isToday()) {
                selectedDate = day.getDate();
                updateSelectedDateLabel(day);
                break;
            }
        }
    }

    private void setupMealsRecycler() {
        mealAdapter = new PlannedMealAdapter(new PlannedMealAdapter.OnMealActionListener() {
            @Override
            public void onRemoveClick(Meal meal) {
                showRemoveConfirmation(meal);
            }

            @Override
            public void onMealClick(Meal meal) {
                Toast.makeText(requireContext(), meal.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMeals.setAdapter(mealAdapter);
    }

    private List<CalendarDayAdapter.CalendarDay> generateWeekDays() {
        List<CalendarDayAdapter.CalendarDay> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int targetStartDay = Calendar.SATURDAY;
        int daysToSubtract = (currentDayOfWeek + 7 - targetStartDay) % 7;
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract);
        for (int i = 0; i < 7; i++) {
            days.add(CalendarDayAdapter.CalendarDay.fromCalendar(calendar));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return days;
    }

    private void updateSelectedDateLabel(CalendarDayAdapter.CalendarDay day) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(inputSdf.parse(day.getDate()));
            binding.textSelectedDate.setText(sdf.format(cal.getTime()));
        } catch (Exception e) {
            binding.textSelectedDate.setText(day.getDayName() + ", " + day.getDayNumber());
        }
    }

    private void updateMealsForSelectedDate() {
        if (mealsByDate != null && selectedDate != null) {
            List<Meal> mealsForDate = mealsByDate.get(selectedDate);
            if (mealsForDate != null && !mealsForDate.isEmpty()) {
                mealAdapter.setMeals(mealsForDate);
                binding.recyclerMeals.setVisibility(View.VISIBLE);
            } else {
                mealAdapter.setMeals(new ArrayList<>());
                binding.recyclerMeals.setVisibility(View.GONE);
            }
        }
    }

    private void showRemoveConfirmation(Meal meal) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Meal")
                .setMessage("Are you sure you want to remove " + meal.getName() + " from your weekly plan?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    presenter.confirmRemoveMeal(meal, selectedDate);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void showLoginRequired() {
        binding.loginRequiredState.setVisibility(View.VISIBLE);
        binding.calendarRecycler.setVisibility(View.GONE);
        binding.divider.setVisibility(View.GONE);
        binding.textSelectedDate.setVisibility(View.GONE);
        binding.recyclerMeals.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
    }

    @Override
    public void showPlannedMeals(Map<String, List<Meal>> mealsByDate) {
        this.mealsByDate = mealsByDate;

        binding.loginRequiredState.setVisibility(View.GONE);
        binding.calendarRecycler.setVisibility(View.VISIBLE);
        binding.divider.setVisibility(View.VISIBLE);
        binding.textSelectedDate.setVisibility(View.VISIBLE);
        binding.recyclerMeals.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);

        updateMealsForSelectedDate();
    }

    @Override
    public void showEmptyState() {
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.loginRequiredState.setVisibility(View.GONE);
        binding.calendarRecycler.setVisibility(View.GONE);
        binding.divider.setVisibility(View.GONE);
        binding.textSelectedDate.setVisibility(View.GONE);
        binding.recyclerMeals.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void removeMealFromList(String date, String mealId) {
        if (date.equals(selectedDate)) {
            mealAdapter.removeMeal(mealId);
            if (mealAdapter.getItemCount() == 0) {
                binding.recyclerMeals.setVisibility(View.GONE);
            }
        }
        if (mealsByDate != null && mealsByDate.containsKey(date)) {
            List<Meal> meals = mealsByDate.get(date);
            if (meals != null) {
                meals.removeIf(meal -> meal.getId().equals(mealId));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        binding = null;
    }
}