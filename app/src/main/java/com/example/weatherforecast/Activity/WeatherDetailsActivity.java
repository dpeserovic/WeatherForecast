package com.example.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.weatherforecast.R;

public class WeatherDetailsActivity extends AppCompatActivity {

    TextView tv_pressure, tv_humidity, tv_visibility, tv_all, tv_speed, tv_deg, tv_sunrise, tv_sunset;
    Button b_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather_details);
        tv_pressure = findViewById(R.id.tv_pressure);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_visibility = findViewById(R.id.tv_visibility);
        tv_all = findViewById(R.id.tv_all);
        tv_speed = findViewById(R.id.tv_speed);
        tv_deg = findViewById(R.id.tv_deg);
        tv_sunrise = findViewById(R.id.tv_sunrise);
        tv_sunset = findViewById(R.id.tv_sunset);
        b_back = findViewById(R.id.b_back);

        Intent i = getIntent();
        String pressure = i.getStringExtra("pressure");
        String humidity = i.getStringExtra("humidity");
        String visibility = i.getStringExtra("visibility");
        String all = i.getStringExtra("all");
        String speed = i.getStringExtra("speed");
        String deg = i.getStringExtra("deg");
        String sunrise = i.getStringExtra("sunrise");
        String sunset = i.getStringExtra("sunset");

        tv_pressure.setText(pressure);
        tv_humidity.setText(humidity);
        tv_visibility.setText(visibility);
        tv_all.setText(all);
        tv_speed.setText(speed);
        tv_deg.setText(deg);
        tv_sunrise.setText(sunrise);
        tv_sunset.setText(sunset);
        b_back.setOnClickListener(v -> OpenMainActivity());
    }

    private void OpenMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}