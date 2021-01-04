package com.example.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.weatherforecast.R;

public class ForecastDetailsActivity extends AppCompatActivity {

    TextView tv_dt, tv_pressure, tv_sea_level, tv_grnd_level, tv_humidity, tv_all, tv_speed, tv_deg;
    Button b_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_details);

        tv_dt = findViewById(R.id.tv_dt);
        tv_pressure = findViewById(R.id.tv_pressure);
        tv_sea_level = findViewById(R.id.tv_sea_level);
        tv_grnd_level = findViewById(R.id.tv_grnd_level);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_all = findViewById(R.id.tv_all);
        tv_speed = findViewById(R.id.tv_speed);
        tv_deg = findViewById(R.id.tv_deg);
        b_back = findViewById(R.id.b_back);

        Intent i = getIntent();
        String dt = i.getStringExtra("dt");
        String pressure = i.getStringExtra("pressure");
        String sea_level = i.getStringExtra("sea_level");
        String grnd_level = i.getStringExtra("grnd_level");
        String humidity = i.getStringExtra("humidity");
        String all = i.getStringExtra("all");
        String speed = i.getStringExtra("speed");
        String deg = i.getStringExtra("deg");

        tv_dt.setText(dt);
        tv_pressure.setText(pressure);
        tv_sea_level.setText(sea_level);
        tv_grnd_level.setText(grnd_level);
        tv_humidity.setText(humidity);
        tv_all.setText(all);
        tv_speed.setText(speed);
        tv_deg.setText(deg);

        b_back.setOnClickListener(v -> OpenMainActivity());
    }

    private void OpenMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}