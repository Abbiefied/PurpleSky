//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

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
        private TextView dayOfWeekTextView;
        private TextView temperatureTextView;
        private TextView uvTextView;
        private TextView locationNameTextView;
        private TextView weatherConTextView;
        private TextView humidityTextView;
        private TextView windSpeedTextView;
        private ImageView weatherIcon;

        private Forecast forecast;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            dayOfWeekTextView = itemView.findViewById(R.id.day_text_view);
            temperatureTextView = itemView.findViewById(R.id.temperature_text_view);
            weatherConTextView = itemView.findViewById(R.id.weather_condition);
            uvTextView = itemView.findViewById(R.id.uv_index_text_view);
            humidityTextView = itemView.findViewById(R.id.humidity_text_view);
            windSpeedTextView = itemView.findViewById(R.id.wind_speed_text_view);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            itemView.setOnClickListener(this);
        }

        public void bind(Forecast forecast) {
            this.forecast = forecast;

            dateTextView.setText(forecast.getDate());
            dayOfWeekTextView.setText(forecast.getDayOfWeek());
            temperatureTextView.setText("Temp: " + String.format("%.1f°C", forecast.getMinTemperatureCelcius()));
            int weatherIconResId = WeatherIconUtils.getWeatherIconResId(forecast.getWeatherCondition(), forecast.getMinTemperatureCelcius());
            weatherIcon.setImageResource(weatherIconResId);
            weatherConTextView.setText(forecast.getWeatherCondition());
            uvTextView.setText("Uv Risk: " + forecast.getUvRisk());
            humidityTextView.setText("Humidity: " + (String.format("%s%%", forecast.getHumidity())));
            windSpeedTextView.setText("Wind: " + String.format("%.1fmph", forecast.getWindSpeed()));
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