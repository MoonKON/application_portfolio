package com.example.gaodesimulation;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import android.location.LocationManager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.content.ServiceConnection;

import android.graphics.RectF;
import android.graphics.Typeface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity implements SensorEventListener{
    Button camera;
    Marker mMarker;
    String mCurrentPhotoPath; // 用于保存拍摄照片的文件路径
    MapView mMapView = null;
    AMap aMap = null;

    //Music part:
    private static SeekBar seek;
    private Timer timer;
    private static TextView tv_progress,tv_total;
    private ImageView music_picture;
    private Button pause;
    private Button playButton;
    private Button end;
    private Button stop;
    private ObjectAnimator animator;
    private Music_Service.MusicControl musicControl;
    //record if the service has been bound, initialize false
    private boolean isUnbind=false;
    private boolean music_pause=false;
    private boolean music_end=false;
    private MyServiceConn conn;
    private MediaPlayer mediaPlayer;
    private Location privLocation;
    private boolean drawLine = false;
    private LocationSource.OnLocationChangedListener mListener;

    private AMapLocationClientOption mLocationOption;
    private int pre_music = R.raw.music0;
    private int now_music = R.raw.music0;
    private int now_picture = R.drawable.music0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private boolean play_click = false;


    //make new label
    private MyLocationStyle myLocationStyle = new MyLocationStyle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //initialize the map
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
        mMapView = (MapView) findViewById(R.id.map);
        //when activity run onCreate, run mMapView.onCreate(saveInstanceState) and create the map
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        // design my own label
        aMap.setMinZoomLevel(12);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // 初始化传感器管理器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 获取加速度传感器和磁场传感器
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // 注册传感器监听器
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);


        //Camera button click listener
        camera = findViewById(R.id.take_photo);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    dispatchTakePictureIntent();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        if (drawLine == true){
            onLocationChanged(aMap);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            // orientation[0] 包含了设备的方向信息，单位为弧度，0代表北，π/2代表东，π代表南，-π/2代表西
            float azimuthInRadians = orientation[0];
            // 将弧度转换为度数
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            // 确保度数在0到360之间
            azimuthInDegrees = (azimuthInDegrees + 360) % 360;
            // 这个就是设备当前的面对方向
            Log.d("Face Direction", "Azimuth: " + azimuthInDegrees);
            // Log.d("Face music", "music: " + music_name);
            if (azimuthInDegrees >= 0 && azimuthInDegrees <= 120){
                now_music = R.raw.music0;
                now_picture = R.drawable.music0;
            }else if(azimuthInDegrees > 120 && azimuthInDegrees <= 240){
                now_music = R.raw.music1;
                now_picture = R.drawable.music1;
            }else if(azimuthInDegrees > 240 && azimuthInDegrees <= 360) {
                now_music = R.raw.music2;
                now_picture = R.drawable.music2;
            }
            if (now_music != pre_music && play_click == true) {
                //if it is time to change the music, change it
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(MainActivity.this, now_music);
                mediaPlayer.start();
                pre_music=now_music;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 在这里处理传感器精度的变化
    }

    private void init(){
        mediaPlayer = MediaPlayer.create(this, now_music);// play music 0/1/2
        playButton = findViewById(R.id.start);
        music_picture = (ImageView)findViewById((R.id.iv_music));
        tv_progress = (TextView)findViewById(R.id.tv_progress);
        tv_total=(TextView)findViewById(R.id.tv_total);
        seek=(SeekBar)findViewById(R.id.seek);
        pause = findViewById(R.id.btn_pause);
        end = findViewById(R.id.btn_end);
        stop = findViewById(R.id.Stop);

        //When click the start button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_click = true;
                drawLine = true;
                if (music_end == true){
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                    animator.start();
                    addTimer();
                }
                else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start(); // 开始播放音乐
                    animator.start();
                    addTimer();
                }
            }
        });

        //When click the pause button
        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (music_pause==false){
                    mediaPlayer.pause();
                    animator.pause();
                    music_pause=true;
                }else{
                    mediaPlayer.start();
                    animator.start();
                    music_pause=false;
                }
            }
        });

        //When click the end button
        end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                music_end=true;
                animator.pause();
                play_click=false;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawLine = false;
            }
        });
        //When click the seek bar
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //when the seekbar comes to the end, animator stop
                if (progress==seekBar.getMax()){
                    animator.pause();//stop animator
                }
            }

            @Override
            //when the seekbar point begin to move
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            //when the seekbar stop moving
            public void onStopTrackingTouch(SeekBar seekBar) {
                //change the process of music along with the movement of seekbar
                int progress=seekBar.getProgress();//get process of seekbar
                mediaPlayer.seekTo(progress);//change process of music
            }
        });

        ImageView iv_music=(ImageView)findViewById(R.id.iv_music);
        //rotation and 0f,360.0f set the animator to rotate from 0 to 360 degrees
        animator=ObjectAnimator.ofFloat(iv_music,"rotation",0f,360.0f);
        animator.setDuration(10000);//time for animator to rotate for a whole cycle is 10s
        animator.setInterpolator(new LinearInterpolator());//constant speed
        animator.setRepeatCount(-1);//-1 means keep rotating
    }

    public void onLocationChanged(AMap aMap){
        Location nowLocation= aMap.getMyLocation();
        if (nowLocation!=null){
            drawLines(nowLocation);
            privLocation = nowLocation;
            //get location information at this time
        }
    }

    public void drawLines(Location curLocation){
        if (privLocation == null) {
            return;
        }
        if (curLocation.getLatitude() != 0.0 && curLocation.getLongitude() != 0.0
                && privLocation.getLongitude() != 0.0 && privLocation.getLatitude() != 0.0) {
            PolylineOptions options = new PolylineOptions();
            //上一个点的经纬度
            options.add(new LatLng(privLocation.getLatitude(), privLocation.getLongitude()));
            //当前的经纬度
            options.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
            options.width(10).geodesic(true).color(Color.BLUE);
            aMap.addPolyline(options);
        }
    }

    public void addTimer(){
        if (timer==null){
            //Build Timer Object
            timer = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run() {
                    if (mediaPlayer==null) return;
                    int duration=mediaPlayer.getDuration();//获取歌曲总时长
                    int currentPosition=mediaPlayer.getCurrentPosition();//获取播放进度
                    Message msg= MainActivity.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    //再将bundle封装到msg消息对象中
                    msg.setData(bundle);
                    //最后将消息发送到主线程的消息队列
                    MainActivity.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒（0.5s）执行一次
            timer.schedule(task,5,500);
        }
    }

    // Get the main thread's Looper
    public static Looper mainLooper = Looper.getMainLooper();

    // Create a Handler using the main thread's Looper
    public static Handler handler = new Handler(mainLooper){
        // Create a Handler using the main thread's Looper
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//// Retrieve music playback progress sent from the child thread
            //Get the current position currentPosition and total duration duration
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            //Set progress bar
            seek.setMax(duration);
            seek.setProgress(currentPosition);
            // Get minutes and seconds of the song
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){// If the minutes of the song are less than 10
                strMinute="0"+minute;// Add a 0 before minutes
            }else{
                strMinute=minute+"";
            }
            if (second<10){
                strSecond="0"+second;
            }else{
                strSecond=second+"";
            }
            // Display the total duration of the song
            tv_total.setText(strMinute+":"+strSecond);
            // Get the current playback time of the song
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){
                strMinute="0"+minute;
            }else{
                strMinute=minute+" ";
            }
            if (second<10){
                strSecond="0"+second;
            }else{
                strSecond=second+" ";
            }
            // Display the current playback time of the song
            tv_progress.setText(strMinute+":"+strSecond);
        }
    };

    class MyServiceConn implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl=(Music_Service.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
        }
    }
    private void unbind(boolean isUnbind){
        //如果解绑了
        if(!isUnbind){
            musicControl.pausePlay();//音乐暂停播放
            unbindService(conn);//解绑服务
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    //run after taking the picture
    private ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null && extras.containsKey("data")) {
                        Bitmap capturedPhotoBitmap = (Bitmap) extras.get("data");
                        saveImageToGallery(capturedPhotoBitmap);
                    }
                }
            }
    );

    //Use camera in phone to take a picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    //save the image in the pictures file
    private void saveImageToGallery(Bitmap bitmap) {
        // 首先创建一个文件对象，用于保存拍摄的照片
        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);

        try {
            // 将 Bitmap 保存到文件中
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 使用 MediaStore 将保存的照片添加到相册中
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "My Image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image saved from my app");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            mCurrentPhotoPath=file.getAbsolutePath();
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // 提示用户照片已保存
            Toast.makeText(this, "照片已保存到相册", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存照片失败", Toast.LENGTH_SHORT).show();
        }
        showPhotoOnMap(BitmapFactory.decodeFile(mCurrentPhotoPath));
    }

    //show the pictures on the map
    private void showPhotoOnMap(Bitmap photoBitmap) {
        if (photoBitmap != null) {
            Location myLocation = aMap.getMyLocation();
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng photoLocation = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(photoLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(photoBitmap));
            aMap.addMarker(markerOptions);
            showAddNoteDialog();
        } else {
            Toast.makeText(this, "加载照片失败", Toast.LENGTH_SHORT).show();
        }
    }

    //click the picture and the add the notation
    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加备注");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String note = input.getText().toString();
                Bitmap testbitmap=textToBitmap(note);
                // 在这里处理添加备注的逻辑
                Toast.makeText(MainActivity.this, "添加备注成功：" + note, Toast.LENGTH_SHORT).show();
                // 在地图上添加标记，显示照片和备注
                Location myLocation = aMap.getMyLocation();
                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();
                LatLng photoLocation = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(photoLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(testbitmap));
                aMap.addMarker(markerOptions);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private Bitmap textToBitmap(String text) {
        // 创建一个画布，宽高为300x100
        Bitmap bitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 设置画布背景色为白色
        canvas.drawColor(Color.WHITE);

        // 创建画笔并设置字体大小、颜色等属性
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);

        // 在画布上绘制文本
        canvas.drawText(text, 30, 65, paint);

        return bitmap;
    }

}