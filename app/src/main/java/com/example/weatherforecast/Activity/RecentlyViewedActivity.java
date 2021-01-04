package com.example.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.weatherforecast.Adapter.RecyclerCityAdapter;
import com.example.weatherforecast.Database.DatabaseClient;
import com.example.weatherforecast.Database.Entity.City;
import com.example.weatherforecast.R;

import java.util.List;

public class RecentlyViewedActivity extends AppCompatActivity {

    private RecyclerView rv_recently_viewed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_viewed);

        rv_recently_viewed = findViewById(R.id.rv_recently_viewed);
        rv_recently_viewed.setLayoutManager(new LinearLayoutManager(this));
        GetCities();
    }

    private void GetCities() {
        class GetCities extends AsyncTask<Void, Void, List<City>> {
            @Override
            protected List<City> doInBackground(Void... voids) {
                List<City> listCities = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .cityDao()
                        .getAllCities();
                return listCities;
            }

            @Override
            protected void onPostExecute(List<City> cityList) {
                super.onPostExecute(cityList);
                RecyclerCityAdapter recyclerCityAdapter = new RecyclerCityAdapter(RecentlyViewedActivity.this, cityList);
                rv_recently_viewed.setAdapter(recyclerCityAdapter);
            }
        }
        GetCities getCities = new GetCities();
        getCities.execute();
    }
}