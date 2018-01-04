package com.example.caihhui.coolweathertest.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.style.UpdateAppearance;

import com.example.caihhui.coolweathertest.Http.HttpUtil;
import com.example.caihhui.coolweathertest.gson.Weather;
import com.example.caihhui.coolweathertest.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UpdateWeather();
        UpdateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60 *60*1000;
        long  time  = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this, WeatherUpdateService.class);
        PendingIntent service = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(service);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,service);
        return super.onStartCommand(intent, flags, startId);
    }
    //更新天气信息
    private void UpdateBingPic() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);
        if (weatherString!=null){

            //有缓存时直接解析天气数据
            final Weather  weather = Utility.WeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weatherResponse = Utility.WeatherResponse(responseText);
                    if (weatherResponse!=null &&"ok".equals(weather.status)){

                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherUpdateService.this).edit();
                        edit.putString("weather",responseText);
                        edit.apply();
                    }
                }
            });
        }

    }
   //更新必应每日一图
    private void UpdateWeather() {
        final String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherUpdateService.this).edit();
                edit.putString("bing_pic",bingPic);
                edit.apply();
            }
        });




    }
}
