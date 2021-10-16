package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class MyWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() { return new MyWallpaperEngine(); }

    class MyWallpaperEngine extends Engine
    {
        private Handler handler = null;
        private Runnable drawRunner = null;
        private int screenWidth = 0, screenHeight = 0;
        private int bmpWidth = 0, bmpHeight = 0;
        private float left = 0f, top = 0f;
        private boolean isVisible, isVideo;

        private MediaPlayer mediaPlayer;
        private Bitmap bmp = null;

        public MyWallpaperEngine()
        {
            getScreenSize();

            isVisible = true;

            Log.i("cool", "Mywe " + isVideo);
            if(!isVideo)
            {
                handler = new Handler();
                drawRunner = new Runnable() {
                    @Override
                    public void run()
                    {
                        draw();
                    }
                };

                processBitmap();

                handler.post(drawRunner);
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder)
        {
            super.onSurfaceCreated(holder);
            if(isVideo)
            {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setSurface(holder.getSurface());
                try
                {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getBaseContext(), MainActivity.imguri);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch (IOException e) { }
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceDestroyed(holder);
            this.isVisible = false;
            if(isVideo)
            {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            else
            {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            this.isVisible = visible;
            if(isVideo)
            {
                if (isVisible)
                {
                    mediaPlayer.start();
                }
                else
                {
                    mediaPlayer.pause();
                }
            }
            else
            {
                if (isVisible)
                {
                    handler.post(drawRunner);
                }
                else
                {
                    handler.removeCallbacks(drawRunner);
                }
            }
        }

        private void draw()
        {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try
            {
                canvas = holder.lockCanvas();
                if (canvas != null)
                {
                    drawBitmap(canvas);
                }
            }
            finally
            {
                if (canvas != null)
                {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            handler.removeCallbacks(drawRunner);
            if (isVisible)
            {
                handler.postDelayed(drawRunner, 10);
            }
        }

        private void drawBitmap(Canvas canvas)
        {
            if(bmp != null)
            {
                //I wanna make some blur effect but i just fucking can't
                canvas.drawBitmap(bmp, (int)left, (int)top, null);
                caclDrawPoint();
            }
        }

        private void getScreenSize()
        {
            try
            {
                this.isVideo = MainActivity.isVideo;
                this.screenWidth = MainActivity.screenWidth;
                this.screenHeight = MainActivity.screenHeight;
            }
            catch (Exception e)
            {
                screenWidth = 0;
                screenHeight = 0;
            }
        }

        private void processBitmap()
        {
            try
            {
                InputStream input = getBaseContext().getContentResolver().openInputStream(MainActivity.imguri);
                bmp = BitmapFactory.decodeStream(input);
                input.close();
            }
            catch (IOException e) { }

            this.bmpWidth = bmp.getWidth();
            this.bmpHeight = bmp.getHeight();

            if(bmpWidth < screenWidth)
            {
                float rate = (screenWidth * 1.5f) / bmpWidth;
                bmp = Bitmap.createScaledBitmap(bmp, (int)(bmpWidth * rate), (int)(bmpHeight * rate), true);
            }
            else if(bmpHeight < screenHeight)
            {
                float rate = (screenHeight * 1.5f) / bmpHeight;
                bmp = Bitmap.createScaledBitmap(bmp, (int)(bmpWidth * rate), (int)(bmpHeight * rate), true);
            }

            this.bmpWidth = bmp.getWidth();
            this.bmpHeight = bmp.getHeight();

            this.left = -((bmpWidth - screenWidth) >> 1);
            this.top = -((bmpHeight - screenHeight) >> 1);
        }

        private void caclDrawPoint()
        {
            if(left + GyroScopeSensor.axisX + bmpWidth > screenWidth && left + GyroScopeSensor.axisX < 0)
            {
                left += GyroScopeSensor.axisX;
            }

            if(top + GyroScopeSensor.axisY + bmpHeight > screenHeight && top + GyroScopeSensor.axisY < 0)
            {
                top += GyroScopeSensor.axisY;
            }

            /*
            xRotation += GyroScopeSensor.axisX;
            yRotation += GyroScopeSensor.axisY;

            matrix.reset();
            camera.save();

            if(yRotation < 90 && yRotation > 0)
            {
                camera.rotateX(yRotation);
            }
            else
            {
                yRotation -= GyroScopeSensor.axisY;
            }

            if(xRotation < 90 && xRotation > 0)
            {
                camera.rotateY(xRotation);
            }
            else
            {
                xRotation -= GyroScopeSensor.axisX;
            }

            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-bmpWidth >> 1, -bmpHeight >> 1);
            matrix.postTranslate(bmpHeight >> 1, bmpHeight >> 1);
            */
        }
    }
}