package com.example.weatherforecast.Retrofit;

import com.example.weatherforecast.Model.Forecast.ForecastResult;
import com.example.weatherforecast.Model.Weather.WeatherResult;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> GetWeatherByCoord(@Query("lat") String lat,
                                                @Query("lon") String lon,
                                                @Query("appid") String appid,
                                                @Query("units") String units);
    @GET("forecast")
    Observable<ForecastResult> GetForecastByCoord(@Query("lat") String lat,
                                                  @Query("lon") String lon,
                                                  @Query("appid") String appid,
                                                  @Query("units") String units);
    @GET("weather")
    Observable<WeatherResult> GetWeatherByCityName(@Query("q") String CityName,
                                                   @Query("appid") String appid,
                                                   @Query("units") String units);
}
