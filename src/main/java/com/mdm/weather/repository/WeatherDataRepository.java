package com.mdm.weather.repository;

import com.mdm.weather.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findByCityIdAndDateBetween(Long cityId, LocalDate startDate, LocalDate endDate);
} 