package com.example.weatherforecast.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherforecast.Adapter.RecyclerForecastAdapter;
import com.example.weatherforecast.Common.Common;
import com.example.weatherforecast.Database.DatabaseClient;
import com.example.weatherforecast.Database.Entity.Favorite;
import com.example.weatherforecast.Model.Forecast.ForecastResult;
import com.example.weatherforecast.R;
import com.example.weatherforecast.Retrofit.IOpenWeatherMap;
import com.example.weatherforecast.Retrofit.RetrofitClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForecastFragment extends Fragment {

    private TextView tv_location, tv_coord;
    private RecyclerView rv_forecast;
    private final IOpenWeatherMap mService;
    private final CompositeDisposable compositeDisposable;

    public static ForecastFragment getInstance(ForecastFragment instance) {
        if(instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);

        tv_location = v.findViewById(R.id.tv_location);
        tv_coord = v.findViewById(R.id.tv_coord);
        rv_forecast = v.findViewById(R.id.rv_forecast);
        rv_forecast.setHasFixedSize(true);
        rv_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        GetForecastInformation();
        return v;
    }

    private void GetForecastInformation() {
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
                    compositeDisposable.add(mService.GetForecastByCoord(
                            String.valueOf(Common.current_location.getLatitude()),
                            String.valueOf(Common.current_location.getLongitude()),
                            Common.APP_ID,
                            Common.units)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ForecastFragment.this::DisplayForecast, throwable -> Log.d("accept: ", "Error: " + throwable.getMessage()))
                    );
                }
                else {
                    Double lat = listFavorite.get(0).getLat();
                    Double lon = listFavorite.get(0).getLon();
                    compositeDisposable.add(mService.GetForecastByCoord(
                            String.valueOf(lat),
                            String.valueOf(lon),
                            Common.APP_ID,
                            Common.units)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ForecastFragment.this::DisplayForecast, throwable -> Log.d("accept: ", "Error: " + throwable.getMessage()))
                    );
                }
            }
        }
        GetFavorites getFavorites = new GetFavorites();
        getFavorites.execute();
    }

    private void DisplayForecast(ForecastResult forecastResult) {
        tv_location.setText(forecastResult.getCity().getCountry() + ", " + forecastResult.getCity().getName());
        tv_coord.setText("LAT: " + forecastResult.getCity().getCoord().getLat() + " | LON: " + forecastResult.getCity().getCoord().getLon());
        RecyclerForecastAdapter adapter = new RecyclerForecastAdapter(forecastResult, getContext());
        rv_forecast.setAdapter(adapter);
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