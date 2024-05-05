package org.me.gcu.adekunle_ganiyat_s2110996;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class WeatherRepositoryTest {

    private WeatherRepository weatherRepository;

    @Before
    public void setUp() {
        weatherRepository = new WeatherRepository(RuntimeEnvironment.getApplication());
    }

    @Test
    public void testFetchWeatherForecast() {
        // Arrange
        String locationId = "1234";

        // Act
        weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {
            @Override
            public void onSuccess(List<Forecast> data) {
                // Assert
                assertNotNull(data);
                assertFalse(data.isEmpty());
            }

            @Override
            public void onFailure(String message) {
                fail("Fetching weather forecast should not fail");
            }
        });
    }
}