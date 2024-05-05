//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<Location> locations;
    private OnLocationClickListener onLocationClickListener;

    public SearchAdapter(List<Location> locations, OnLocationClickListener onLocationClickListener) {
        this.locations = locations;
        this.onLocationClickListener = onLocationClickListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_suggestion, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void updateData(List<Location> newLocations) {
        locations.clear();
        locations.addAll(newLocations);
        notifyDataSetChanged();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView locationNameTextView;
        private Location location;

        public SearchViewHolder(@NonNull View itemView) {
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