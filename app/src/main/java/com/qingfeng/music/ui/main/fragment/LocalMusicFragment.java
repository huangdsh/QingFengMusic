package com.qingfeng.music.ui.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingfeng.music.Constant;
import com.qingfeng.music.R;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.service.PlayerService;
import com.qingfeng.music.service.connection.BaseServiceConnection;
import com.qingfeng.music.ui.main.contract.LocalMainContract;
import com.qingfeng.music.ui.main.model.LocalMainModel;
import com.qingfeng.music.ui.main.presenter.LocalMainPresenter;
import com.qingfeng.music.util.Player;
import com.wgl.android.library.base.BaseFragment;
import com.wgl.android.library.baseadapter.BaseRecyclerAdapter;
import com.wgl.android.library.commonutils.LogUtils;
import com.wgl.android.library.viewholder.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Ganlin.Wu on 2016/9/28.
 */
public class LocalMusicFragment extends BaseFragment<LocalMainPresenter, LocalMainModel> implements LocalMainContract.View {
    private List<Music> mMusicList = new ArrayList<>();
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private BaseRecyclerAdapter<Music> mRecyclerAdapter;
    private static ColorStateList sColorStatePlaying;
    private static ColorStateList sColorStateNotPlaying;
    private BroadcastReceiver mReceiver;
    private BaseServiceConnection<PlayerService> mPlayerServiceConnection;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_list;
    }


    @Override
    protected void initView() {
        initializeColorStateLists();
        mRecyclerAdapter = new BaseRecyclerAdapter<Music>(getContext(), mMusicList, R.layout.media_list_item) {
            @Override
            public void convert(final RecyclerViewHolder holder, final Music data, final int position) {

                ImageView imageView = holder.findViewById(R.id.play_eq);
                TextView titleView = holder.findViewById(R.id.title);
                TextView artistView = holder.findViewById(R.id.description);
                titleView.setText(data.getTitle());
                artistView.setText(data.getArtist());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayerService service = mPlayerServiceConnection.getService();
                        if (service != null) {
                            service.play(mMusicList, position);
                        }
                    }
                });

                Music music = Player.getPlayer().getMusic();
                if (music == data) {
                    switch (Player.getPlayer().getState()) {
                        case Player.STATE_STOP:
                            Drawable stopDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_play_arrow_gray_36dp);
                            DrawableCompat.setTintList(stopDrawable, sColorStateNotPlaying);
                            imageView.setImageDrawable(stopDrawable);
                            break;
                        case Player.STATE_PLAY:
                            AnimationDrawable animation = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_equalizer_white_36dp);
                            DrawableCompat.setTintList(animation, sColorStatePlaying);
                            imageView.setImageDrawable(animation);
                            animation.start();
                            break;
                        case Player.STATE_PAUSE:
                            Drawable pauseDrawable = ContextCompat.getDrawable(getContext(),
                                    R.drawable.ic_equalizer1_white_36dp);
                            DrawableCompat.setTintList(pauseDrawable, sColorStatePlaying);
                            imageView.setImageDrawable(pauseDrawable);
                            break;
                    }
                } else {
                    Drawable stopDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_play_arrow_gray_36dp);
                    DrawableCompat.setTintList(stopDrawable, sColorStateNotPlaying);
                    imageView.setImageDrawable(stopDrawable);
                }

            }
        };
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Intent intent = new Intent(getContext(), PlayerService.class);
        mPlayerServiceConnection = new BaseServiceConnection<>();
        getContext().bindService(intent, mPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        mReceiver = new LocalMusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RECEIVER_MUSIC_CHANGE);
        intentFilter.addAction(Constant.RECEIVER_MUSIC_SCAN_SUCCESS);
        intentFilter.addAction(Constant.RECEIVER_UPDATE_MUSIC_COLLECT);
        intentFilter.addAction(Constant.RECEIVER_UPDATE_MUSIC_LIST);
        getContext().registerReceiver(mReceiver, intentFilter);
        mPresenter.loadLocalMusics();
    }

    private void initializeColorStateLists() {
        sColorStateNotPlaying = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.media_item_icon_not_playing));
        sColorStatePlaying = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.media_item_icon_playing));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
        getContext().unbindService(mPlayerServiceConnection);
    }


    @Override
    public void showMusicList(List<Music> musics) {
        mMusicList.clear();
        mMusicList.addAll(musics);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }

    private class LocalMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.logd("onReceive: " + intent.getAction());
            if (Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())) {
                mRecyclerAdapter.notifyDataSetChanged();
            } else if (Constant.RECEIVER_MUSIC_SCAN_SUCCESS.equals(intent.getAction())
                    || Constant.RECEIVER_UPDATE_MUSIC_LIST.equals(intent.getAction())) {
                mPresenter.loadLocalMusics();
            }
        }
    }


}
