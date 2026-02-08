package com.example.yum_yum.presentation.weaklyMeals;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yum_yum.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.DayViewHolder> {
    private final List<CalendarDay> days;
    private int selectedPosition = 0;
    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day, int position);
    }

    public CalendarDayAdapter(List<CalendarDay> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).isToday()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        holder.bind(day, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardDay;
        private final TextView textDayName;
        private final TextView textDayNumber;
        private final View indicatorDot;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDay = itemView.findViewById(R.id.card_day);
            textDayName = itemView.findViewById(R.id.text_day_name);
            textDayNumber = itemView.findViewById(R.id.text_day_number);
            indicatorDot = itemView.findViewById(R.id.indicator_dot);
        }

        public void bind(CalendarDay day, boolean isSelected) {
            textDayName.setText(day.getDayName());
            textDayNumber.setText(String.valueOf(day.getDayNumber()));
            indicatorDot.setVisibility(day.hasMeals() ? View.VISIBLE : View.GONE);

            if (isSelected) {
                cardDay.setCardBackgroundColor(itemView.getContext().getColor(R.color.primary));
                cardDay.setStrokeColor(itemView.getContext().getColor(R.color.primary));
                textDayName.setTextColor(itemView.getContext().getColor(R.color.primary));
                textDayNumber.setTextColor(itemView.getContext().getColor(R.color.primary));
            } else {
                cardDay.setCardBackgroundColor(Color.WHITE);
                cardDay.setStrokeColor(itemView.getContext().getColor(R.color.background_color));
                textDayName.setTextColor(itemView.getContext().getColor(R.color.text_hint));
                textDayNumber.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            }

            cardDay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDayClick(day, getAdapterPosition());
                }
            });
        }
    }

    public static class CalendarDay {
        private final String date;
        private final String dayName;
        private final int dayNumber;
        private final boolean isToday;
        private boolean hasMeals;

        public CalendarDay(String date, String dayName, int dayNumber, boolean isToday) {
            this.date = date;
            this.dayName = dayName;
            this.dayNumber = dayNumber;
            this.isToday = isToday;
            this.hasMeals = false;
        }

        public String getDate() {
            return date;
        }

        public String getDayName() {
            return dayName;
        }

        public int getDayNumber() {
            return dayNumber;
        }

        public boolean isToday() {
            return isToday;
        }

        public boolean hasMeals() {
            return hasMeals;
        }

        public void setHasMeals(boolean hasMeals) {
            this.hasMeals = hasMeals;
        }

        public static CalendarDay fromCalendar(Calendar calendar) {
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayNameSdf = new SimpleDateFormat("EEE", Locale.getDefault());

            String date = dateSdf.format(calendar.getTime());
            String dayName = dayNameSdf.format(calendar.getTime());
            int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);

            Calendar today = Calendar.getInstance();
            boolean isToday = dateSdf.format(today.getTime()).equals(date);

            return new CalendarDay(date, dayName, dayNumber, isToday);
        }
    }
}