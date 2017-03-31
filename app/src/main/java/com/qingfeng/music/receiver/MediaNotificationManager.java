package com.qingfeng.music.receiver;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.qingfeng.music.Constant;
import com.qingfeng.music.R;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.service.PlayerService;
import com.qingfeng.music.ui.player.activity.FullScreenPlayerActivity;
import com.qingfeng.music.util.Player;
import com.qingfeng.music.util.ResourceHelper;
import com.wgl.android.library.commonutils.LogUtils;

import java.util.concurrent.ExecutionException;


/**
 * Created by Ganlin.Wu on 2016/9/30.
 */
public class MediaNotificationManager extends BroadcastReceiver {
    private static final String TAG = "MediaNotification";
    private static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "com.qingfeng.music.playerservice.pause";
    public static final String ACTION_PLAY = "com.qingfeng.music.playerservice.play";
    public static final String ACTION_PREV = "com.qingfeng.music.playerservice.prev";
    public static final String ACTION_NEXT = "com.qingfeng.music.playerservice.next";

    private final PlayerService mService;


    private NotificationManager mNotificationManager;

    private PendingIntent mPauseIntent;
    private PendingIntent mPlayIntent;
    private PendingIntent mPreviousIntent;
    private PendingIntent mNextIntent;

    private int mNotificationColor;

    private boolean mStarted = false;

    public MediaNotificationManager(PlayerService service) {
        mService = service;
        mNotificationColor = ResourceHelper.getThemeColor(mService,
                android.R.attr.colorPrimary, Color.DKGRAY);

        mNotificationManager = (NotificationManager) mService
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String pkg = mService.getPackageName();

        mPauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationManager.cancelAll();
    }

    public void startNotification() {
        Log.d(TAG, "startNotification: ");
        if (!mStarted) {
            // The notification must be updated after setting started to true
            Notification notification = createNotification();
            if (notification != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_NEXT);
                filter.addAction(ACTION_PAUSE);
                filter.addAction(ACTION_PLAY);
                filter.addAction(ACTION_PREV);
                filter.addAction(Constant.RECEIVER_MUSIC_CHANGE);
                mService.registerReceiver(this, filter);

                mService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    private Notification createNotification() {


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService);

        int playPauseButtonPosition = 0;


        notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
                mService.getString(R.string.label_previous), mPreviousIntent);

        // If there is a "skip to previous" button, the play/pause button will
        // be the second one. We need to keep track of it, because the MediaStyle notification
        // requires to specify the index of the buttons (actions) that should be visible
        // when in compact view.
        playPauseButtonPosition = 1;


        addPlayPauseAction(notificationBuilder);

        // If skip to next action is enabled

        notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
                mService.getString(R.string.label_next), mNextIntent);


        Music music = Player.getPlayer().getMusic();

        String fetchArtUrl = null;
        Bitmap art = null;
        if (music.getImageUrl() != null) {
            // This sample assumes the iconUri will be a valid URL formatted String, but
            // it can actually be any valid Android Uri formatted String.
            // async fetch the album art icon
            if (art == null) {
                // use a placeholder art while the remote art is being downloaded
                art = BitmapFactory.decodeResource(mService.getResources(),
                        R.drawable.ic_default_art);
            } else {
                try {
                    art = Glide.with(mService).load(music.getImageUrl()).asBitmap().centerCrop()
                            .into(54, 54).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        notificationBuilder
                .setStyle(new NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(
                                new int[]{playPauseButtonPosition})  // show only play/pause in compact view
                )
                .setColor(mNotificationColor)
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setContentIntent(createContentIntent())
                .setContentTitle(music.getTitle())
                .setContentText(music.getArtist())
                .setLargeIcon(art);

        setNotificationPlaybackState(notificationBuilder);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(mService.getResources(), R.drawable.ic_notification));
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        return notificationBuilder.build();
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {

        String label;
        int icon;
        PendingIntent intent;
        if (Player.getPlayer().getState() == Player.STATE_PLAY) {
            label = mService.getString(R.string.label_pause);
            icon = R.drawable.uamp_ic_pause_white_24dp;
            intent = mPauseIntent;
        } else {
            label = mService.getString(R.string.label_play);
            icon = R.drawable.uamp_ic_play_arrow_white_24dp;
            intent = mPlayIntent;
        }
        builder.addAction(new NotificationCompat.Action(icon, label, intent));
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, FullScreenPlayerActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        Player player = Player.getPlayer();
        if (player == null || !mStarted) {
            mService.stopForeground(true);
            return;
        }
        if (player.getState() == Player.STATE_PLAY
                && player.getPosition() >= 0) {

            builder.setWhen(System.currentTimeMillis() - player.getPosition())
                    .setShowWhen(true)
                    .setUsesChronometer(true);
        } else {
            builder
                    .setWhen(0)
                    .setShowWhen(false)
                    .setUsesChronometer(false);
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(player.getState() == Player.STATE_PLAY);
    }

    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            mService.stopForeground(true);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_PAUSE:
                LogUtils.logd("ACTION_PAUSE");
                mService.pause();
                break;
            case ACTION_PLAY:
                LogUtils.logd("ACTION_PLAY");
                if (Player.getPlayer().getState() == Player.STATE_STOP) {
                    mService.play();
                } else {
                    mService.replay();
                }
                break;
            case ACTION_NEXT:
                LogUtils.logd("ACTION_NEXT");
                mService.next();
                break;
            case ACTION_PREV:
                LogUtils.logd("ACTION_PREV");
                mService.previous();
                break;
            case Constant.RECEIVER_MUSIC_CHANGE:
                if (Player.getPlayer().getState() == Player.STATE_STOP) {
                    stopNotification();
                } else {
                    Notification notification = createNotification();
                    if (notification != null) {
                        mNotificationManager.notify(NOTIFICATION_ID, notification);
                    }
                }
            default:
                break;
        }
    }
}
