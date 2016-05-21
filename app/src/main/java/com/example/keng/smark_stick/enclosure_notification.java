package com.example.keng.smark_stick;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by keng on 2016/5/21.
 */
public class enclosure_notification {

    public int show = 0;
    private Context mcontext = null;
    private NotificationManager notificationManager = null;
    private Map<Integer,Notification> mNotifications = null;

    public enclosure_notification(Context context) {
        mcontext = context;
        //获得通知系统服务
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifications = new HashMap<Integer,Notification>();
    }

    public void showNotification(){

        //判断通知是否已经显示
        if(show!=444){
            //创建通知对象
            Notification notification = new Notification();
            //设置滚动文字
            notification.tickerText = "老人超出预设范围，请注意老人安全";
            //设置图标
            notification.icon = R.drawable.stick_notifition;
            //添加通知特性
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            //设置点击通知栏的操作
            Intent intent = new Intent(mcontext,MainActivity.class);
            PendingIntent pintent = PendingIntent.getActivities(mcontext,0, new Intent[]{intent},0);
            notification.contentIntent = pintent;

            //创建RemotViews对象
            RemoteViews remoteViews = new RemoteViews(mcontext.getPackageName(),R.layout.enclosure_notification);

            //设置Notification的视图
            notification.contentView = remoteViews;

            //设置手机震动提醒功能
            notification.defaults|=Notification.DEFAULT_VIBRATE;
            long[] vibrate = {0,1000};
            notification.vibrate = vibrate;

            //发出通知
            notificationManager.notify(444,notification);
            show = 444;
        }
    }

    //取消通知
    public void cancelNotification(){
        notificationManager.cancel(333);
        show = 0;
    }
}
