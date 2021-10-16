package com.example.myapplication;

import static android.content.Context.SENSOR_SERVICE;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class GyroScopeSensor
{
    SensorManager sensorManager;
    Sensor sensor = null;

    public static float axisX = 0;
    public static float axisY = 0;

    float sensorRate = 0.05f;
    float sensorIgnore = 0.2f;
    SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            axisY = rad2deg(event.values[0]) * sensorRate;
            axisX = rad2deg(event.values[1]) * sensorRate;

            if (abs(axisX) < sensorIgnore)
            {
                axisX = 0;
            }

            if(abs(axisY) < sensorIgnore)
            {
                axisY = 0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //nope
        }
    };

    private float rad2deg(float rad)
    {
        return (float) (rad * 180 / Math.PI);
    }
}
