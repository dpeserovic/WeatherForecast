package com.example.weatherforecast.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.Activity.RecentlyViewedCityActivity;
import com.example.weatherforecast.Database.Entity.City;
import com.example.weatherforecast.R;

import java.util.List;

public class RecyclerCityAdapter extends RecyclerView.Adapter<RecyclerCityAdapter.CityViewHolder> {

    private final Context mContext;
    private final List<City> listCity;

    public RecyclerCityAdapter(Context mContext, List<City> listCity) {
        this.mContext = mContext;
        this.listCity = listCity;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_city, parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = listCity.get(position);
        holder.tv_location.setText(city.getName());
    }

    @Override
    public int getItemCount() {
        return listCity.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder {
        TextView tv_location;
        View view;

        public CityViewHolder(@NonNull View v) {
            super(v);
            tv_location = v.findViewById(R.id.tv_location);
            view = v;
            view.setOnClickListener(v1 -> {
                Intent i = new Intent(mContext, RecentlyViewedCityActivity.class);
                i.putExtra("tv_location", tv_location.getText().toString());
                mContext.startActivity(i);
            });
        }
    }
}