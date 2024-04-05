package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<Location> locations;
    private OnLocationClickListener onLocationClickListener;

    public LocationAdapter(List<Location> locations, OnLocationClickListener onLocationClickListener) {
        this.locations = locations;
        this.onLocationClickListener = onLocationClickListener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView locationNameTextView;
        private Location location;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.location_name_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(Location location) {
            this.location = location;
            locationNameTextView.setText(location.getName());
        }

        @Override
        public void onClick(View v) {
            if (onLocationClickListener != null) {
                onLocationClickListener.onLocationClick(location);
            }
        }
    }

    public interface OnLocationClickListener {
        void onLocationClick(Location location);
    }
}