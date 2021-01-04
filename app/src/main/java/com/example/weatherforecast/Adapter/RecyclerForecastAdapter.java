package com.example.weatherforecast.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.Activity.ForecastDetailsActivity;
import com.example.weatherforecast.Common.Common;
import com.example.weatherforecast.Model.Forecast.ForecastResult;
import com.example.weatherforecast.R;
import com.squareup.picasso.Picasso;

public class RecyclerForecastAdapter extends RecyclerView.Adapter<RecyclerForecastAdapter.ForecastViewHolder> {

    private final ForecastResult forecastResult;
    private final Context context;

    public RecyclerForecastAdapter(ForecastResult forecastResult, Context context) {
        this.forecastResult = forecastResult;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerForecastAdapter.ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_forecast, parent, false);
        return new ForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerForecastAdapter.ForecastViewHolder holder, int position) {
        Picasso.get().load("https://openweathermap.org/img/wn/"+forecastResult.getList().get(position).getWeather().get(0).getIcon() + ".png").into(holder.iv_icon);
        holder.tv_dt.setText(Common.convertUnixToDate(forecastResult.getList().get(position).getDt()));
        holder.tv_temp.setText(Math.round(forecastResult.getList().get(position).getMain().getTemp()) + "°C");
        holder.dt.setText(Common.convertUnixToLongDate(forecastResult.getList().get(position).getDt()));
        holder.pressure.setText(Math.round(forecastResult.getList().get(position).getMain().getPressure()) + "hPa");
        holder.sea_level.setText(Math.round(forecastResult.getList().get(position).getMain().getSea_level()) + "hPa");
        holder.grnd_level.setText(Math.round(forecastResult.getList().get(position).getMain().getGrnd_level()) + "hPa");
        holder.humidity.setText(Math.round(forecastResult.getList().get(position).getMain().getHumidity()) + "%");
        holder.all.setText(forecastResult.getList().get(position).getClouds().getAll() + "%");
        holder.speed.setText(forecastResult.getList().get(position).getWind().getSpeed() + "m/s");
        double cardinal_direction;
        cardinal_direction = Math.round(forecastResult.getList().get(position).getWind().getDeg()/10.0)*10;
        if((cardinal_direction == 350) || (cardinal_direction == 360) || (cardinal_direction == 0) || (cardinal_direction == 10)) {
            holder.deg.setText(R.string.north);
        }
        if((cardinal_direction == 20) || (cardinal_direction == 30)) {
            holder.deg.setText(R.string.north_northeast);
        }
        if((cardinal_direction == 40) || (cardinal_direction == 50)) {
            holder.deg.setText(R.string.northeast);
        }
        if((cardinal_direction == 60) || (cardinal_direction == 70)) {
            holder.deg.setText(R.string.east_northeast);
        }
        if((cardinal_direction == 80) || (cardinal_direction == 90) || (cardinal_direction == 100)) {
            holder.deg.setText(R.string.east);
        }
        if((cardinal_direction == 110) || (cardinal_direction == 120)) {
            holder.deg.setText(R.string.east_southeast);
        }
        if((cardinal_direction == 130) || (cardinal_direction == 140)) {
            holder.deg.setText(R.string.southeast);
        }
        if((cardinal_direction == 150) || (cardinal_direction == 160)) {
            holder.deg.setText(R.string.south_southeast);
        }
        if((cardinal_direction == 170) || (cardinal_direction == 180) || (cardinal_direction == 190)) {
            holder.deg.setText(R.string.south);
        }
        if((cardinal_direction == 200) || (cardinal_direction == 210)) {
            holder.deg.setText(R.string.south_southwest);
        }
        if((cardinal_direction == 220) || (cardinal_direction == 230)) {
            holder.deg.setText(R.string.southwest);
        }
        if((cardinal_direction == 240) || (cardinal_direction == 250)) {
            holder.deg.setText(R.string.west_southwest);
        }
        if((cardinal_direction == 260) || (cardinal_direction == 270) || (cardinal_direction == 280)) {
            holder.deg.setText(R.string.west);
        }
        if((cardinal_direction == 290) || (cardinal_direction == 300)) {
            holder.deg.setText(R.string.west_northwest);
        }
        if((cardinal_direction == 310) || (cardinal_direction == 320)) {
            holder.deg.setText(R.string.northwest);
        }
        if((cardinal_direction == 330) || (cardinal_direction == 340)) {
            holder.deg.setText(R.string.north_northwest);
        }
    }

    @Override
    public int getItemCount() {
        return forecastResult.getList().size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tv_dt, tv_temp;
        ImageView iv_icon;
        TextView dt, pressure, sea_level, grnd_level, humidity, all, speed, deg;
        View view;

        public ForecastViewHolder(@NonNull View v) {
            super(v);
            tv_dt = v.findViewById(R.id.tv_dt);
            iv_icon = v.findViewById(R.id.iv_icon);
            tv_temp = v.findViewById(R.id.tv_temp);
            dt = v.findViewById(R.id.dt);
            pressure = v.findViewById(R.id.pressure);
            sea_level = v.findViewById(R.id.sea_level);
            grnd_level = v.findViewById(R.id.grnd_level);
            humidity = v.findViewById(R.id.humidity);
            all = v.findViewById(R.id.all);
            speed = v.findViewById(R.id.speed);
            deg = v.findViewById(R.id.deg);
            view = v;
            view.setOnClickListener(v1 -> {
                Intent intent = new Intent(context, ForecastDetailsActivity.class);
                intent.putExtra("dt", dt.getText().toString());
                intent.putExtra("pressure", pressure.getText().toString());
                intent.putExtra("sea_level", sea_level.getText().toString());
                intent.putExtra("grnd_level", grnd_level.getText().toString());
                intent.putExtra("humidity", humidity.getText().toString());
                intent.putExtra("all", all.getText().toString());
                intent.putExtra("speed", speed.getText().toString());
                intent.putExtra("deg", deg.getText().toString());
                context.startActivity(intent);
                ((Activity)context).finish();
            });
        }
    }
}