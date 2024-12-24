package com.mdm.weather.service.impl;

import com.mdm.weather.entity.City;
import com.mdm.weather.exception.ResourceNotFoundException;
import com.mdm.weather.repository.CityRepository;
import com.mdm.weather.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City updateCity(Long id, City city) {
        City existingCity = getCityById(id);
        existingCity.setName(city.getName());
        existingCity.setProvince(city.getProvince());
        existingCity.setDescription(city.getDescription());
        return cityRepository.save(existingCity);
    }

    @Override
    public void deleteCity(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new ResourceNotFoundException("City not found with id: " + id);
        }
        cityRepository.deleteById(id);
    }

    @Override
    public City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
} 