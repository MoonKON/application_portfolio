package com.example.gaodesimulation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Message;
import java.util.Timer;
import java.util.TimerTask;

public class Music_Service extends Service {

    private MediaPlayer player;
    private Timer timer;
    public Music_Service() {} //constructor

    @Override
    //used to bind the communication channel between Activity and Service
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }
    @Override
    //initialize
    public void onCreate(){
        super.onCreate(); //create music player
        player = new MediaPlayer();
    }

    public void addTimer(){
        if (timer==null){
            //Build Timer Object
            timer = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run() {
                    if (player==null) return;
                    int duration=player.getDuration();//获取歌曲总时长
                    int currentPosition=player.getCurrentPosition();//获取播放进度
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
    //Binder是一种跨进程的通信方式
    class MusicControl extends Binder{
        public void play(int i){//String path
            Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/"+"music"+0);
            try{
                //重置音乐播放器
                player.reset();
                //加载多媒体文件
                player=MediaPlayer.create(getApplicationContext(),uri);
                player.start();//播放音乐
                addTimer();//添加计时器
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        //下面的暂停继续和退出方法全部调用的是MediaPlayer自带的方法
        public void pausePlay(){
            player.pause();//暂停播放音乐
        }
        public void continuePlay(){
            player.start();//继续播放音乐
        }
        public void seekTo(int progress){
            player.seekTo(progress);//设置音乐的播放位置
        }
    }
    //销毁多媒体播放器
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player==null) return;
        if(player.isPlaying()) player.stop();//停止播放音乐
        player.release();//释放占用的资源
        player=null;//将player置为空
    }
}