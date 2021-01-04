package com.example.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforecast.Common.Common;
import com.example.weatherforecast.Database.DatabaseClient;
import com.example.weatherforecast.Database.Entity.City;
import com.example.weatherforecast.Database.Entity.Favorite;
import com.example.weatherforecast.Model.Weather.WeatherResult;
import com.example.weatherforecast.R;
import com.example.weatherforecast.Retrofit.IOpenWeatherMap;
import com.example.weatherforecast.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ManageCitiesActivity extends AppCompatActivity {

    TextView tv_location, tv_temp, tv_temp_max_min, tv_feels_like, tv_dt;
    TextView lon, lat, country, name;
    ImageView iv_icon;
    Button b_set_as_default;
    ConstraintLayout cl_manage_cities;
    ProgressBar pb_manage_cities;
    IOpenWeatherMap mService;
    CompositeDisposable compositeDisposable;
    private MaterialSearchBar msb_manage_cities;
    static ManageCitiesActivity instance;

    public static ManageCitiesActivity getInstance() {
        if(instance == null)
            instance = new ManageCitiesActivity();
        return instance;
    }

    public ManageCitiesActivity() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cities);

        tv_location = findViewById(R.id.tv_location);
        tv_temp = findViewById(R.id.tv_temp);
        tv_temp_max_min = findViewById(R.id.tv_temp_max_min);
        tv_feels_like = findViewById(R.id.tv_feels_like);
        tv_dt = findViewById(R.id.tv_dt);
        iv_icon = findViewById(R.id.iv_icon);
        b_set_as_default = findViewById(R.id.b_set_as_default);
        lon = findViewById(R.id.lon);
        lat = findViewById(R.id.lat);
        country = findViewById(R.id.country);
        name = findViewById(R.id.name);
        cl_manage_cities = findViewById(R.id.cl_manage_cities);
        pb_manage_cities = findViewById(R.id.pb_manage_cities);
        msb_manage_cities = findViewById(R.id.msb_manage_cities);
        msb_manage_cities.setEnabled(false);
        new LoadCities().execute();
        b_set_as_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Double dLon = Double.parseDouble(lon.getText().toString());
                final Double dLat = Double.parseDouble(lat.getText().toString());
                final String sCountry = country.getText().toString();
                final String sName = name.getText().toString();

                class FavoriteCity extends AsyncTask<Void, Void, Void> {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Favorite favorite = new Favorite();
                        favorite.setLon(dLon);
                        favorite.setLat(dLat);
                        favorite.setCountry(sCountry);
                        favorite.setName(sName);
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .favoriteDao()
                                .insertOrUpdateFavorite(favorite);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent i = new Intent(ManageCitiesActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                FavoriteCity favoriteCity = new FavoriteCity();
                favoriteCity.execute();
            }
        });
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            List<String> listCities = new ArrayList<>();
            try {
                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String read;
                while((read = bufferedReader.readLine()) != null)
                    stringBuilder.append(read);
                listCities = new Gson().fromJson(stringBuilder.toString(), new TypeToken<List<String>>(){}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listCities;
        }

        @Override
        protected void onSuccess(final List<String> listCities) {
            super.onSuccess(listCities);
            msb_manage_cities.setEnabled(true);
            msb_manage_cities.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for(String search : listCities) {
                        if(search.toLowerCase().contains(msb_manage_cities.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    msb_manage_cities.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            msb_manage_cities.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    GetWeatherInformation(text.toString());
                    msb_manage_cities.setLastSuggestions(listCities);
                    HideKeyboard();
                }

                @Override
                public void onButtonClicked(int buttonCode) {
                }
            });
            msb_manage_cities.setLastSuggestions(listCities);
            pb_manage_cities.setVisibility(View.GONE);
        }
    }

    private void GetWeatherInformation(String cityName) {
        compositeDisposable.add(mService.GetWeatherByCityName(cityName,
                Common.APP_ID,
                Common.units)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::DisplayWeather, throwable -> Log.d("ManageCitiesActivity", "ERROR:" + throwable.getMessage())));
    }

    private void DisplayWeather(WeatherResult weatherResult) {
        Picasso.get().load("https://openweathermap.org/img/wn/" +
                weatherResult.getWeather().get(0).getIcon() +
                ".png").into(iv_icon);

        tv_location.setText(weatherResult.getSys().getCountry() + ", " + weatherResult.getName());
        tv_temp.setText(Math.round(weatherResult.getMain().getTemp())+"°C");
        tv_temp_max_min.setText(Math.round(weatherResult.getMain().getTemp_max())+"°C / "+Math.round(weatherResult.getMain().getTemp_min())+"°C");
        tv_feels_like.setText(Math.round(weatherResult.getMain().getFeels_like())+"°C");
        tv_dt.setText(Common.convertUnixToHour(weatherResult.getDt()));

        lon.setText(String.valueOf(weatherResult.getCoord().getLon()));
        lat.setText(String.valueOf(weatherResult.getCoord().getLat()));
        country.setText(String.valueOf(weatherResult.getSys().getCountry()));
        name.setText(String.valueOf(weatherResult.getName()));

        cl_manage_cities.setVisibility(View.VISIBLE);
        pb_manage_cities.setVisibility(View.GONE);

        final double lon;
        lon = weatherResult.getCoord().getLon();
        final double lat;
        lat = weatherResult.getCoord().getLat();
        final String country;
        country = weatherResult.getSys().getCountry();
        final String name;
        name = weatherResult.getName();

        Log.d("ManageCitiesActivity", lon + "/" + lat + " " + country + " " + name);

        class SaveCity extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                City city = new City();
                city.setLon(lon);
                city.setLat(lat);
                city.setCountry(country);
                city.setName(name);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .cityDao()
                        .insertCity(city);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
            }
        }
        SaveCity saveCity = new SaveCity();
        saveCity.execute();
    }

    public void HideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}