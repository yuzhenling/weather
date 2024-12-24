package com.mdm.weather.repository;

import com.mdm.weather.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByName(String name);
} 