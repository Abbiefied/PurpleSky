package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.RecentLocation;

import java.util.List;

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.RecentLocationViewHolder> {

    private List<RecentLocation> recentLocations;
    private OnRecentLocationClickListener onRecentLocationClickListener;

    public RecentLocationAdapter(List<RecentLocation> recentLocations, OnRecentLocationClickListener onRecentLocationClickListener) {
        this.recentLocations = recentLocations;
        this.onRecentLocationClickListener = onRecentLocationClickListener;
    }

    @NonNull
    @Override
    public RecentLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_location, parent, false);
        return new RecentLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentLocationViewHolder holder, int position) {
        RecentLocation recentLocation = recentLocations.get(position);
        holder.bind(recentLocation);
    }

    @Override
    public int getItemCount() {
        return recentLocations.size();
    }

    public class RecentLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView locationNameTextView;
        private TextView weatherSummaryTextView;
        private RecentLocation recentLocation;

        public RecentLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.location_name_text_view);
            weatherSummaryTextView = itemView.findViewById(R.id.weather_summary_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(RecentLocation recentLocation) {
            this.recentLocation = recentLocation;
            locationNameTextView.setText(recentLocation.getLocationName());
            weatherSummaryTextView.setText(recentLocation.getWeatherSummary());
        }

        @Override
        public void onClick(View v) {
            if (onRecentLocationClickListener != null) {
                onRecentLocationClickListener.onRecentLocationClick(recentLocation);
            }
        }
    }

    public void setRecentLocations(List<RecentLocation> recentLocations) {
        this.recentLocations = recentLocations;
        notifyDataSetChanged();
    }

    public interface OnRecentLocationClickListener {
        void onRecentLocationClick(RecentLocation recentLocation);
    }
}
