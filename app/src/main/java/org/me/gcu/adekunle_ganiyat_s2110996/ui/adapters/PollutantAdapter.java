package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollutantAdapter extends RecyclerView.Adapter<PollutantAdapter.PollutantViewHolder> {

    private List<AirQualityData.Pollutant> pollutants;
    private Map<Integer, Boolean> expandedStates = new HashMap<>(); // Track expanded/collapsed state

    public PollutantAdapter(List<AirQualityData.Pollutant> pollutants) {
        this.pollutants = pollutants;
    }

    @NonNull
    @Override
    public PollutantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pollutant, parent, false);
        return new PollutantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollutantViewHolder holder, int position) {
        AirQualityData.Pollutant pollutant = pollutants.get(position);
        holder.bind(pollutant, expandedStates.getOrDefault(position, false));
    }

    @Override
    public int getItemCount() {
        return pollutants.size();
    }

    class PollutantViewHolder extends RecyclerView.ViewHolder {
        private TextView pollutantDisplayName;
        private TextView pollutantFullName;
        private TextView pollutantConcentration;
        private ProgressBar pollutantConcentrationBar;
        private TextView pollutantAdditionalInfo;
        private ImageView expandCollapseIcon;

        PollutantViewHolder(@NonNull View itemView) {
            super(itemView);
            pollutantDisplayName = itemView.findViewById(R.id.pollutant_display_name);
            pollutantFullName = itemView.findViewById(R.id.pollutant_full_name);
            pollutantConcentration = itemView.findViewById(R.id.pollutant_concentration);
            pollutantConcentrationBar = itemView.findViewById(R.id.pollutant_concentration_bar);
            pollutantAdditionalInfo = itemView.findViewById(R.id.pollutant_additional_info);
            expandCollapseIcon = itemView.findViewById(R.id.expand_collapse_icon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    boolean isExpanded = !expandedStates.getOrDefault(position, false);
                    expandedStates.put(position, isExpanded);
                    bind(pollutants.get(position), isExpanded);
                }
            });
        }

        void bind(AirQualityData.Pollutant pollutant, boolean isExpanded) {
            pollutantDisplayName.setText(pollutant.getDisplayName());
            pollutantFullName.setText(pollutant.getFullName());

            AirQualityData.Concentration concentration = pollutant.getConcentration();
            pollutantConcentration.setText(String.format("%.2f %s", concentration.getValue(), concentration.getUnits()));

            // Adjust the ProgressBar max and progress based on your concentration range
            pollutantConcentrationBar.setMax(50);
            pollutantConcentrationBar.setProgress((int) (concentration.getValue() / 10));

            AirQualityData.AdditionalInfo additionalInfo = pollutant.getAdditionalInfo();
            StringBuilder infoBuilder = new StringBuilder();
            if (additionalInfo.getSources() != null) {
                infoBuilder.append("Sources: ").append(additionalInfo.getSources()).append("\n");
            }
            if (additionalInfo.getEffects() != null) {
                infoBuilder.append("Effects: ").append(additionalInfo.getEffects());
            }
            pollutantAdditionalInfo.setText(infoBuilder.toString());

            pollutantAdditionalInfo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            expandCollapseIcon.setRotation(isExpanded ? 180f : 0f);
        }
    }
}