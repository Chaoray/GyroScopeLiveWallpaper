package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static boolean isVideo = false;
    public int PICK_FILE = 20;
    public static @Nullable Uri imguri = null;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();

        findViewById(R.id.SetWallpaperButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(imguri != null)
                {
                    WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try
                    {
                        myWallpaperManager.clear();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getBaseContext(), MyWallpaperService.class));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Please choose picture first", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.ChoosePictureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isVideo = false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_FILE);
            }
        });

        findViewById(R.id.ChooseVideoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isVideo = true;
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), PICK_FILE);
            }
        });

        findViewById(R.id.CleanWallpaperButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                try
                {
                    myWallpaperManager.clear();
                }
                catch (Exception e)
                {

                }
            }
        });

        checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        try
        {
            if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null)
            {
                //Toast.makeText(this, data.getData().getPath(), Toast.LENGTH_LONG).show();
                if(!isVideo)
                {
                    img.setImageURI(data.getData());
                }
                imguri = data.getData();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error:Fail to choose picture", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Init()
    {
        MyWallpaperService ws = new MyWallpaperService();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        GyroScopeSensor g = new GyroScopeSensor();
        g.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        g.sensor = g.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        g.sensorManager.registerListener(g.gyroscopeSensorListener, g.sensor, SensorManager.SENSOR_DELAY_FASTEST);

        img = findViewById(R.id.PreviewImage);
    }

    private void checkPermission()
    {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}