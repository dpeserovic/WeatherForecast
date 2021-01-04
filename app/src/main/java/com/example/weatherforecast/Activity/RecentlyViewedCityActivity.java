package com.example.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weatherforecast.Common.Common;
import com.example.weatherforecast.Model.Weather.WeatherResult;
import com.example.weatherforecast.R;
import com.example.weatherforecast.Retrofit.IOpenWeatherMap;
import com.example.weatherforecast.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RecentlyViewedCityActivity extends AppCompatActivity {

    private TextView tv_name, tv_temp, tv_temp_max_min, tv_feels_like, tv_dt;
    private ImageView iv_icon;
    private Button b_back;

    ConstraintLayout cl_weather;
    ProgressBar pb_weather;

    IOpenWeatherMap mService;
    CompositeDisposable compositeDisposable;

    static RecentlyViewedCityActivity instance;

    public static RecentlyViewedCityActivity getInstance() {
        if(instance == null)
            instance = new RecentlyViewedCityActivity();
        return instance;
    }

    public RecentlyViewedCityActivity() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_viewed_city);

        tv_name = findViewById(R.id.tv_name);
        tv_temp = findViewById(R.id.tv_temp);
        tv_temp_max_min = findViewById(R.id.tv_temp_max_min);
        tv_feels_like = findViewById(R.id.tv_feels_like);
        tv_dt = findViewById(R.id.tv_dt);

        iv_icon = findViewById(R.id.iv_icon);

        b_back = findViewById(R.id.b_back);

        cl_weather = findViewById(R.id.cl_weather);
        pb_weather = findViewById(R.id.pb_weather);

        Intent intent = getIntent();
        String name = intent.getStringExtra("tv_location");

        tv_name.setText(name);

        getWeatherInformation();

        b_back.setOnClickListener(v -> OpenRecentlyViewedActivity());
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.GetWeatherByCityName(tv_name.getText().toString(),
                Common.APP_ID,
                Common.units)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> displayWeather(weatherResult), new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ManageCityActivity", "ERROR:" + throwable.getMessage());
                    }
                }));
    }

    private void displayWeather(WeatherResult weatherResult) {
        Picasso.get().load(("https://openweathermap.org/img/wn/")+weatherResult.getWeather().get(0).getIcon()+".png").into(iv_icon);

        tv_name.setText(weatherResult.getSys().getCountry() + " " + weatherResult.getName());
        tv_temp.setText(Math.round(weatherResult.getMain().getTemp()) + "°C");
        tv_temp_max_min.setText(Math.round(weatherResult.getMain().getTemp_max()) + "°C / " + Math.round(weatherResult.getMain().getTemp_min()) + "°C");
        tv_feels_like.setText(Math.round(weatherResult.getMain().getFeels_like()) + "°C");
        tv_dt.setText(Common.convertUnixToHour(weatherResult.getDt()));

        cl_weather.setVisibility(View.VISIBLE);
        pb_weather.setVisibility(View.GONE);
    }

    private void OpenRecentlyViewedActivity() {
        Intent intent = new Intent(this, RecentlyViewedActivity.class);
        startActivity(intent);
        finish();
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