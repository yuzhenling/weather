package com.mdm.weather.service;

import com.mdm.weather.entity.City;
import java.util.List;

public interface CityService {
    City createCity(City city);
    City updateCity(Long id, City city);
    void deleteCity(Long id);
    City getCityById(Long id);
    List<City> getAllCities();
} 