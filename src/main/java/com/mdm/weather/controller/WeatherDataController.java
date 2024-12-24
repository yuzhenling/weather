package com.mdm.weather.controller;

import com.mdm.weather.common.ResponseResult;
import com.mdm.weather.entity.City;
import com.mdm.weather.entity.WeatherData;
import com.mdm.weather.service.CityService;
import com.mdm.weather.service.WeatherDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/weather-data")
@RequiredArgsConstructor
public class WeatherDataController {
    private final WeatherDataService weatherDataService;
    private final CityService cityService;

    @PostMapping
    public ResponseResult<WeatherData> createWeatherData(@RequestBody WeatherData weatherData) {
        return ResponseResult.success(weatherDataService.createWeatherData(weatherData));
    }

    @PutMapping("/{id}")
    public ResponseResult<WeatherData> updateWeatherData(@PathVariable Long id, @RequestBody WeatherData weatherData) {
        return ResponseResult.success(weatherDataService.updateWeatherData(id, weatherData));
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteWeatherData(@PathVariable Long id) {
        weatherDataService.deleteWeatherData(id);
        return ResponseResult.success(null);
    }

    @GetMapping("/{id}")
    public ResponseResult<WeatherData> getWeatherDataById(@PathVariable Long id) {
        return ResponseResult.success(weatherDataService.getWeatherDataById(id));
    }

    @GetMapping("/city/{cityId}")
    public ResponseResult<List<WeatherData>> getWeatherDataByCity(
            @PathVariable Long cityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseResult.success(
                weatherDataService.getWeatherDataByCityAndDateRange(cityId, startDate, endDate)
        );
    }

    @PostMapping("/upload")
    public ResponseResult<String> uploadWeatherData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseResult.error("请选择文件");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<WeatherData> weatherDataList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // 跳过CSV头行
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;
                
                try {
                    WeatherData weatherData = new WeatherData();
                    
                    // 假设CSV格式：城市ID,日期,温度,湿度,天气,风向,风速
                    City city = cityService.getCityById(Long.parseLong(data[0].trim()));
                    weatherData.setCity(city);
                    weatherData.setDate(LocalDate.parse(data[1].trim(), formatter));
                    weatherData.setTemperature(Double.parseDouble(data[2].trim()));
                    weatherData.setHumidity(Double.parseDouble(data[3].trim()));
                    weatherData.setWeather(data[4].trim());
                    weatherData.setWindDirection(data[5].trim());
                    weatherData.setWindSpeed(Double.parseDouble(data[6].trim()));
                    
                    weatherDataList.add(weatherData);
                } catch (Exception e) {
                    // 记录错误但继续处理
                    continue;
                }
            }
            
            // 批量保存数据
            weatherDataService.saveAll(weatherDataList);
            
            return ResponseResult.success("成功导入 " + weatherDataList.size() + " 条数据");
        } catch (Exception e) {
            return ResponseResult.error("文件处理失败：" + e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseResult<Long> getWeatherDataCount() {
        return ResponseResult.success(weatherDataService.count());
    }
} 