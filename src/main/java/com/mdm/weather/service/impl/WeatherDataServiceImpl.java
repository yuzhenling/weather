package com.mdm.weather.service.impl;

import com.mdm.weather.entity.WeatherData;
import com.mdm.weather.exception.ResourceNotFoundException;
import com.mdm.weather.repository.WeatherDataRepository;
import com.mdm.weather.service.WeatherDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherDataServiceImpl implements WeatherDataService {
    private final WeatherDataRepository weatherDataRepository;

    @Override
    public WeatherData createWeatherData(WeatherData weatherData) {
        return weatherDataRepository.save(weatherData);
    }

    @Override
    public WeatherData updateWeatherData(Long id, WeatherData weatherData) {
        WeatherData existingData = getWeatherDataById(id);
        existingData.setCity(weatherData.getCity());
        existingData.setDate(weatherData.getDate());
        existingData.setTemperature(weatherData.getTemperature());
        existingData.setHumidity(weatherData.getHumidity());
        existingData.setWeather(weatherData.getWeather());
        existingData.setWindDirection(weatherData.getWindDirection());
        existingData.setWindSpeed(weatherData.getWindSpeed());
        return weatherDataRepository.save(existingData);
    }

    @Override
    public void deleteWeatherData(Long id) {
        if (!weatherDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Weather data not found with id: " + id);
        }
        weatherDataRepository.deleteById(id);
    }

    @Override
    public WeatherData getWeatherDataById(Long id) {
        return weatherDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather data not found with id: " + id));
    }

    @Override
    public List<WeatherData> getWeatherDataByCityAndDateRange(Long cityId, LocalDate startDate, LocalDate endDate) {
        return weatherDataRepository.findByCityIdAndDateBetween(cityId, startDate, endDate);
    }

    @Override
    public void saveAll(List<WeatherData> weatherDataList) {
        weatherDataRepository.saveAll(weatherDataList);
    }

    @Override
    public long count() {
        return weatherDataRepository.count();
    }
} 