package com.example.weatherforecast.Database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weatherforecast.Database.Entity.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    List<Favorite> getAllFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateFavorite(Favorite favorite);
}