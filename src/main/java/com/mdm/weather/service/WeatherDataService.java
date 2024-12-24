package com.mdm.weather.service;

import com.mdm.weather.entity.WeatherData;
import java.time.LocalDate;
import java.util.List;

public interface WeatherDataService {
    WeatherData createWeatherData(WeatherData weatherData);
    WeatherData updateWeatherData(Long id, WeatherData weatherData);
    void deleteWeatherData(Long id);
    WeatherData getWeatherDataById(Long id);
    List<WeatherData> getWeatherDataByCityAndDateRange(Long cityId, LocalDate startDate, LocalDate endDate);

    void saveAll(List<WeatherData> weatherDataList);

    long count();
} 