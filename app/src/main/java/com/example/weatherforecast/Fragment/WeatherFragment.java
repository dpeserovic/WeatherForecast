package com.example.weatherforecast.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weatherforecast.Activity.WeatherDetailsActivity;
import com.example.weatherforecast.Common.Common;
import com.example.weatherforecast.Database.DatabaseClient;
import com.example.weatherforecast.Database.Entity.Favorite;
import com.example.weatherforecast.Model.Weather.WeatherResult;
import com.example.weatherforecast.R;
import com.example.weatherforecast.Retrofit.IOpenWeatherMap;
import com.example.weatherforecast.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class WeatherFragment extends Fragment {

    private TextView tv_location, tv_temp, tv_temp_max_min, tv_feels_like, tv_dt;
    private ImageView iv_icon;
    private TextView pressure, humidity, visibility, all, speed, deg, sunrise, sunset;
    private ConstraintLayout cl_weather;
    private ProgressBar pb_weather;
    private final IOpenWeatherMap mService;
    private final CompositeDisposable compositeDisposable;

    public static WeatherFragment getInstance(WeatherFragment instance) {
        if(instance==null)
            instance = new WeatherFragment();
        return instance;
    }

    public WeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather, container, false);

        cl_weather = v.findViewById(R.id.cl_weather);
        tv_location = v.findViewById(R.id.tv_location);
        tv_temp = v.findViewById(R.id.tv_temp);
        tv_temp_max_min = v.findViewById(R.id.tv_temp_max_min);
        tv_feels_like = v.findViewById(R.id.tv_feels_like);
        tv_dt = v.findViewById(R.id.tv_dt);
        iv_icon = v.findViewById(R.id.iv_icon);
        pressure = v.findViewById(R.id.pressure);
        humidity = v.findViewById(R.id.humidity);
        visibility = v.findViewById(R.id.visibility);
        all = v.findViewById(R.id.all);
        speed = v.findViewById(R.id.speed);
        deg = v.findViewById(R.id.deg);
        sunrise = v.findViewById(R.id.sunrise);
        sunset = v.findViewById(R.id.sunset);
        pb_weather = v.findViewById(R.id.pb_weather);
        GetWeatherInformation();
        Button b_details;
        b_details = v.findViewById(R.id.b_details);
        b_details.setOnClickListener(v1 -> {
            Intent i = new Intent(getActivity(), WeatherDetailsActivity.class);
            i.putExtra("pressure", pressure.getText().toString());
            i.putExtra("humidity", humidity.getText().toString());
            i.putExtra("visibility", visibility.getText().toString());
            i.putExtra("all", all.getText().toString());
            i.putExtra("speed", speed.getText().toString());
            i.putExtra("deg", deg.getText().toString());
            i.putExtra("sunrise", sunrise.getText().toString());
            i.putExtra("sunset", sunset.getText().toString());
            startActivity(i);
            getActivity().finish();
        });
        return v;
    }

    public void GetWeatherInformation() {
        class GetFavorites extends AsyncTask<Void, Void, List<Favorite>> {
            @Override
            protected List<Favorite> doInBackground(Void... voids) {
                List<Favorite> listFavorite = DatabaseClient
                        .getInstance(getContext())
                        .getAppDatabase()
                        .favoriteDao()
                        .getAllFavorites();
                return listFavorite;
            }

            @Override
            protected void onPostExecute(List<Favorite> listFavorite) {
                super.onPostExecute(listFavorite);
                if(listFavorite.size() == 0) {
                    compositeDisposable.add(mService.GetWeatherByCoord(String.valueOf(Common.current_location.getLatitude()),
                            String.valueOf(Common.current_location.getLongitude()),
                            Common.APP_ID,
                            Common.units)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(WeatherFragment.this::DisplayWeather, throwable -> Log.d("accept: ", "Error: " + throwable.getMessage())));
                }
                else {
                    Double lat = listFavorite.get(0).getLat();
                    Double lon = listFavorite.get(0).getLon();
                    compositeDisposable.add(mService.GetWeatherByCoord(String.valueOf(lat),
                            String.valueOf(lon),
                            Common.APP_ID,
                            Common.units)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(WeatherFragment.this::DisplayWeather, throwable -> Log.d("accept: ", "Error: " + throwable.getMessage())));
                }
            }
        }
        GetFavorites getFavorites = new GetFavorites();
        getFavorites.execute();
    }

    private void DisplayWeather(WeatherResult weatherResult) {
        Picasso.get().load(("https://openweathermap.org/img/wn/")+weatherResult.getWeather().get(0).getIcon() + ".png").into(iv_icon);
        tv_location.setText(weatherResult.getSys().getCountry() + " " + weatherResult.getName());
        tv_temp.setText(Math.round(weatherResult.getMain().getTemp()) + "°C");
        tv_temp_max_min.setText(Math.round(weatherResult.getMain().getTemp_max()) + "°C / "+Math.round(weatherResult.getMain().getTemp_min()) + "°C");
        tv_feels_like.setText(Math.round(weatherResult.getMain().getFeels_like()) + "°C");
        tv_dt.setText(Common.convertUnixToHour(weatherResult.getDt()));
        pressure.setText(weatherResult.getMain().getPressure() + "hPa");
        humidity.setText(weatherResult.getMain().getHumidity() + "%");
        visibility.setText(weatherResult.getVisibility() + "m");
        all.setText(weatherResult.getClouds().getAll() + "%");
        speed.setText(weatherResult.getWind().getSpeed() + "m/s");
        double cardinal_direction;
        cardinal_direction = Math.round(weatherResult.getWind().getDeg() / 10.0) * 10;
        if ((cardinal_direction == 350) || (cardinal_direction == 360) || (cardinal_direction == 0) || (cardinal_direction == 10)) {
            deg.setText(R.string.north);
        }
        if ((cardinal_direction == 20) || (cardinal_direction == 30)) {
            deg.setText(R.string.north_northeast);
        }
        if ((cardinal_direction == 40) || (cardinal_direction == 50)) {
            deg.setText(R.string.northeast);
        }
        if ((cardinal_direction == 60) || (cardinal_direction == 70)) {
            deg.setText(R.string.east_northeast);
        }
        if ((cardinal_direction == 80) || (cardinal_direction == 90) || (cardinal_direction == 100)) {
            deg.setText(R.string.east);
        }
        if ((cardinal_direction == 110) || (cardinal_direction == 120)) {
            deg.setText(R.string.east_southeast);
        }
        if ((cardinal_direction == 130) || (cardinal_direction == 140)) {
            deg.setText(R.string.southeast);
        }
        if ((cardinal_direction == 150) || (cardinal_direction == 160)) {
            deg.setText(R.string.south_southeast);
        }
        if ((cardinal_direction == 170) || (cardinal_direction == 180) || (cardinal_direction == 190)) {
            deg.setText(R.string.south);
        }
        if ((cardinal_direction == 200) || (cardinal_direction == 210)) {
            deg.setText(R.string.south_southwest);
        }
        if ((cardinal_direction == 220) || (cardinal_direction == 230)) {
            deg.setText(R.string.southwest);
        }
        if ((cardinal_direction == 240) || (cardinal_direction == 250)) {
            deg.setText(R.string.west_southwest);
        }
        if ((cardinal_direction == 260) || (cardinal_direction == 270) || (cardinal_direction == 280)) {
            deg.setText(R.string.west);
        }
        if ((cardinal_direction == 290) || (cardinal_direction == 300)) {
            deg.setText(R.string.west_northwest);
        }
        if ((cardinal_direction == 310) || (cardinal_direction == 320)) {
            deg.setText(R.string.northwest);
        }
        if ((cardinal_direction == 330) || (cardinal_direction == 340)) {
            deg.setText(R.string.north_northwest);
        }
        sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
        sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
        cl_weather.setVisibility(View.VISIBLE);
        pb_weather.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}