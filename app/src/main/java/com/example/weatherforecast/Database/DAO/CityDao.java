package com.example.weatherforecast.Database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weatherforecast.Database.Entity.City;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SElECT * FROM cities")
    List<City> getAllCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(City city);
}