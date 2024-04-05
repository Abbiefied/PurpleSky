package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<Forecast> forecastList;
    private OnForecastClickListener onForecastClickListener;

    public ForecastAdapter(List<Forecast> forecastList, OnForecastClickListener onForecastClickListener) {
        this.forecastList = forecastList;
        this.onForecastClickListener = onForecastClickListener;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecast forecast = forecastList.get(position);
        holder.bind(forecast);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView dateTextView;
        private TextView temperatureTextView;
        private Forecast forecast;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            temperatureTextView = itemView.findViewById(R.id.temperature_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(Forecast forecast) {
            this.forecast = forecast;
            dateTextView.setText(forecast.getDate());
            temperatureTextView.setText(String.format("%.1f°C - %.1f°C", forecast.getMinTemperatureCelcius(), forecast.getMaxTemperatureCelcius()));
        }

        @Override
        public void onClick(View v) {
            if (onForecastClickListener != null) {
                onForecastClickListener.onForecastClick(forecast);
            }
        }
    }

    public void setForecastList(List<Forecast> forecastList) {
        this.forecastList = forecastList;
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    public interface OnForecastClickListener {
        void onForecastClick(Forecast forecast);
    }
}