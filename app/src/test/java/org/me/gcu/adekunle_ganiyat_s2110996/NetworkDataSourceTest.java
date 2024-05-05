package org.me.gcu.adekunle_ganiyat_s2110996;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.NetworkDataSource;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class NetworkDataSourceTest {

    @Test
    public void testParseAirQualityJson() {
        // Arrange
        String jsonResponse = "{\n" +
                "  \"dateTime\": \"2024-05-05T07:00:00Z\",\n" +
                "  \"regionCode\": \"gb\",\n" +
                "  \"indexes\": [\n" +
                "    {\n" +
                "      \"code\": \"uaqi\",\n" +
                "      \"displayName\": \"Universal AQI\",\n" +
                "      \"aqi\": 80,\n" +
                "      \"aqiDisplay\": \"80\",\n" +
                "      \"color\": {\n" +
                "        \"red\": 0.34509805,\n" +
                "        \"green\": 0.74509805,\n" +
                "        \"blue\": 0.20784314\n" +
                "      },\n" +
                "      \"category\": \"Excellent air quality\",\n" +
                "      \"dominantPollutant\": \"o3\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"gbr_defra\",\n" +
                "      \"displayName\": \"DAQI (UK)\",\n" +
                "      \"aqi\": 2,\n" +
                "      \"aqiDisplay\": \"2\",\n" +
                "      \"color\": {\n" +
                "        \"red\": 0.19215687,\n" +
                "        \"green\": 1.0\n" +
                "      },\n" +
                "      \"category\": \"Low air pollution\",\n" +
                "      \"dominantPollutant\": \"o3\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"pollutants\": [\n" +
                "    {\n" +
                "      \"code\": \"co\",\n" +
                "      \"displayName\": \"CO\",\n" +
                "      \"fullName\": \"Carbon monoxide\",\n" +
                "      \"concentration\": {\n" +
                "        \"value\": 165.57,\n" +
                "        \"units\": \"PARTS_PER_BILLION\"\n" +
                "      },\n" +
                "      \"additionalInfo\": {\n" +
                "        \"sources\": \"Typically originates from incomplete combustion of carbon fuels, such as that which occurs in car engines and power plants.\",\n" +
                "        \"effects\": \"When inhaled, carbon monoxide can prevent the blood from carrying oxygen. Exposure may cause dizziness, nausea and headaches. Exposure to extreme concentrations can lead to loss of consciousness.\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"no2\",\n" +
                "      \"displayName\": \"NO2\",\n" +
                "      \"fullName\": \"Nitrogen dioxide\",\n" +
                "      \"concentration\": {\n" +
                "        \"value\": 10.77,\n" +
                "        \"units\": \"PARTS_PER_BILLION\"\n" +
                "      },\n" +
                "      \"additionalInfo\": {\n" +
                "        \"sources\": \"Main sources are fuel burning processes, such as those used in industry and transportation.\",\n" +
                "        \"effects\": \"Exposure may cause increased bronchial reactivity in patients with asthma, lung function decline in patients with Chronic Obstructive Pulmonary Disease (COPD), and increased risk of respiratory infections, especially in young children.\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"o3\",\n" +
                "      \"displayName\": \"O3\",\n" +
                "      \"fullName\": \"Ozone\",\n" +
                "      \"concentration\": {\n" +
                "        \"value\": 24.99,\n" +
                "        \"units\": \"PARTS_PER_BILLION\"\n" +
                "      },\n" +
                "      \"additionalInfo\": {\n" +
                "        \"sources\": \"Ozone is created in a chemical reaction between atmospheric oxygen, nitrogen oxides, carbon monoxide and organic compounds, in the presence of sunlight.\",\n" +
                "        \"effects\": \"Ozone can irritate the airways and cause coughing, a burning sensation, wheezing and shortness of breath. Additionally, ozone is one of the major components of photochemical smog.\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        NetworkDataSource dataSource = new NetworkDataSource();

        // Act
        AirQualityData airQualityData = dataSource.parseAirQualityJson(jsonResponse);

        // Assert
        assertNotNull(airQualityData);
    }
}
