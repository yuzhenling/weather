package com.mdm.weather.controller;

import com.mdm.weather.common.ResponseResult;
import com.mdm.weather.entity.City;
import com.mdm.weather.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @PostMapping
    public ResponseResult<City> createCity(@RequestBody City city) {
        return ResponseResult.success(cityService.createCity(city));
    }

    @PutMapping("/{id}")
    public ResponseResult<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        return ResponseResult.success(cityService.updateCity(id, city));
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseResult.success(null);
    }

    @GetMapping("/{id}")
    public ResponseResult<City> getCityById(@PathVariable Long id) {
        return ResponseResult.success(cityService.getCityById(id));
    }

    @GetMapping("/count")
    public ResponseResult<Integer> getCityCount() {
        List<City> cities = cityService.getAllCities();
        return ResponseResult.success(cities.size());
    }

    @GetMapping
    public ResponseResult<List<City>> getAllCities() {
        return ResponseResult.success(cityService.getAllCities());
    }

 
} 