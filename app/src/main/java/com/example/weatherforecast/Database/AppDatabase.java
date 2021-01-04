package com.example.weatherforecast.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.weatherforecast.Database.DAO.CityDao;
import com.example.weatherforecast.Database.DAO.FavoriteDao;
import com.example.weatherforecast.Database.Entity.City;
import com.example.weatherforecast.Database.Entity.Favorite;

@Database(entities = {City.class, Favorite.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao cityDao();
    public abstract FavoriteDao favoriteDao();
}