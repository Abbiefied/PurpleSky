package org.me.gcu.adekunle_ganiyat_s2110996.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AirQualityData implements Parcelable {
    private String dateTime;
    private String regionCode;
    private List<AirQualityIndex> indexes;
    private List<Pollutant> pollutants;

    public AirQualityData() {
        // Default constructor
    }

    protected AirQualityData(Parcel in) {
        dateTime = in.readString();
        regionCode = in.readString();
        indexes = in.createTypedArrayList(AirQualityIndex.CREATOR);
        pollutants = in.createTypedArrayList(Pollutant.CREATOR);
    }

    public static final Creator<AirQualityData> CREATOR = new Creator<AirQualityData>() {
        @Override
        public AirQualityData createFromParcel(Parcel in) {
            return new AirQualityData(in);
        }

        @Override
        public AirQualityData[] newArray(int size) {
            return new AirQualityData[size];
        }
    };

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public List<AirQualityIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<AirQualityIndex> indexes) {
        this.indexes = indexes;
    }

    public List<Pollutant> getPollutants() {
        return pollutants;
    }

    public void setPollutants(List<Pollutant> pollutants) {
        this.pollutants = pollutants;
    }

    public String getAirQualityIndex() {
        // Find the desired air quality index (e.g., "uaqi" or "gbr_defra")
        for (AirQualityIndex index : indexes) {
            if (index.getCode().equals("uaqi")) {
                return index.getAqiDisplay();
            }
        }
        return "N/A";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateTime);
        dest.writeString(regionCode);
        dest.writeTypedList(indexes);
        dest.writeTypedList(pollutants);
    }

    public static class AirQualityIndex implements Parcelable {
        private String code;
        private String displayName;
        private int aqi;
        private String aqiDisplay;
        private Color color;
        private String category;
        private String dominantPollutant;

        public AirQualityIndex() {
            // Default constructor
        }

        protected AirQualityIndex(Parcel in) {
            code = in.readString();
            displayName = in.readString();
            aqi = in.readInt();
            aqiDisplay = in.readString();
            color = in.readParcelable(Color.class.getClassLoader());
            category = in.readString();
            dominantPollutant = in.readString();
        }

        public static final Creator<AirQualityIndex> CREATOR = new Creator<AirQualityIndex>() {
            @Override
            public AirQualityIndex createFromParcel(Parcel in) {
                return new AirQualityIndex(in);
            }

            @Override
            public AirQualityIndex[] newArray(int size) {
                return new AirQualityIndex[size];
            }
        };

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public int getAqi() {
            return aqi;
        }

        public void setAqi(int aqi) {
            this.aqi = aqi;
        }

        public String getAqiDisplay() {
            return aqiDisplay;
        }

        public void setAqiDisplay(String aqiDisplay) {
            this.aqiDisplay = aqiDisplay;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDominantPollutant() {
            return dominantPollutant;
        }

        public void setDominantPollutant(String dominantPollutant) {
            this.dominantPollutant = dominantPollutant;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(code);
            dest.writeString(displayName);
            dest.writeInt(aqi);
            dest.writeString(aqiDisplay);
            dest.writeParcelable(color, flags);
            dest.writeString(category);
            dest.writeString(dominantPollutant);
        }
    }

    public static class Pollutant implements Parcelable {
        private String code;
        private String displayName;
        private String fullName;
        private Concentration concentration;
        private AdditionalInfo additionalInfo;

        public Pollutant() {
            // Default constructor
        }

        protected Pollutant(Parcel in) {
            code = in.readString();
            displayName = in.readString();
            fullName = in.readString();
            concentration = in.readParcelable(Concentration.class.getClassLoader());
            additionalInfo = in.readParcelable(AdditionalInfo.class.getClassLoader());
        }

        public static final Creator<Pollutant> CREATOR = new Creator<Pollutant>() {
            @Override
            public Pollutant createFromParcel(Parcel in) {
                return new Pollutant(in);
            }

            @Override
            public Pollutant[] newArray(int size) {
                return new Pollutant[size];
            }
        };

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Concentration getConcentration() {
            return concentration;
        }

        public void setConcentration(Concentration concentration) {
            this.concentration = concentration;
        }

        public AdditionalInfo getAdditionalInfo() {
            return additionalInfo;
        }

        public void setAdditionalInfo(AdditionalInfo additionalInfo) {
            this.additionalInfo = additionalInfo;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(code);
            dest.writeString(displayName);
            dest.writeString(fullName);
            dest.writeParcelable(concentration, flags);
            dest.writeParcelable(additionalInfo, flags);
        }
    }

    public static class Concentration implements Parcelable {
        private double value;
        private String units;

        public Concentration() {
            // Default constructor
        }

        protected Concentration(Parcel in) {
            value = in.readDouble();
            units = in.readString();
        }

        public static final Creator<Concentration> CREATOR = new Creator<Concentration>() {
            @Override
            public Concentration createFromParcel(Parcel in) {
                return new Concentration(in);
            }

            @Override
            public Concentration[] newArray(int size) {
                return new Concentration[size];
            }
        };

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(value);
            dest.writeString(units);
        }
    }

    public static class AdditionalInfo implements Parcelable {
        private String sources;
        private String effects;

        public AdditionalInfo() {
            // Default constructor
        }

        protected AdditionalInfo(Parcel in) {
            sources = in.readString();
            effects = in.readString();
        }

        public static final Creator<AdditionalInfo> CREATOR = new Creator<AdditionalInfo>() {
            @Override
            public AdditionalInfo createFromParcel(Parcel in) {
                return new AdditionalInfo(in);
            }

            @Override
            public AdditionalInfo[] newArray(int size) {
                return new AdditionalInfo[size];
            }
        };

        public String getSources() {
            return sources;
        }

        public void setSources(String sources) {
            this.sources = sources;
        }

        public String getEffects() {
            return effects;
        }

        public void setEffects(String effects) {
            this.effects = effects;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sources);
            dest.writeString(effects);
        }
    }

    public static class Color implements Parcelable {
        private double red;
        private double green;
        private double blue;

        public Color() {
            // Default constructor
        }

        protected Color(Parcel in) {
            red = in.readDouble();
            green = in.readDouble();
            blue = in.readDouble();
        }

        public static final Creator<Color> CREATOR = new Creator<Color>() {
            @Override
            public Color createFromParcel(Parcel in) {
                return new Color(in);
            }

            @Override
            public Color[] newArray(int size) {
                return new Color[size];
            }
        };

        public double getRed() {
            return red;
        }

        public void setRed(double red) {
            this.red = red;
        }

        public double getGreen() {
            return green;
        }

        public void setGreen(double green) {
            this.green = green;
        }

        public double getBlue() {
            return blue;
        }

        public void setBlue(double blue) {
            this.blue = blue;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(red);
            dest.writeDouble(green);
            dest.writeDouble(blue);
        }
    }
}