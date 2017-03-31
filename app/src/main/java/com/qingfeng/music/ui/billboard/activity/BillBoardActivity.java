package com.qingfeng.music.ui.billboard.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qingfeng.music.Constant;
import com.qingfeng.music.R;
import com.qingfeng.music.adapter.LoadMoreRecyclerAdapter;
import com.qingfeng.music.dao.Music;
import com.qingfeng.music.service.DownloadSongService;
import com.qingfeng.music.service.PlayerService;
import com.qingfeng.music.service.connection.BaseServiceConnection;
import com.qingfeng.music.ui.billboard.contract.BillboardContract;
import com.qingfeng.music.ui.billboard.model.BillboardModel;
import com.qingfeng.music.ui.billboard.presenter.BillboardPresenter;
import com.qingfeng.music.widget.recyclerview.RecycleViewDivider;
import com.wgl.android.library.base.BaseActivity;
import com.wgl.android.library.base.BaseService;
import com.wgl.android.library.commonwidget.LoadingTip;
import com.wgl.android.library.viewholder.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class BillboardActivity extends BaseActivity<BillboardPresenter, BillboardModel> implements BillboardContract.View {
    private static final String TAG = "BillBoardActivity";
    public static final String FLAG_TITLE = "title";
    public static final String FLAG_TYPE = "type";
    private static final int COUNT = 50;
    private String mTitle;
    private int mType;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.backdrop)
    ImageView mImageView;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.loading_tip)
    LoadingTip mLoadingTip;
    private List<Music> mMusicList = new ArrayList<>();
    private LoadMoreRecyclerAdapter<Music> mRecyclerAdapter;
    private BaseServiceConnection<PlayerService> mPlayServiceConnection;
    private BaseServiceConnection<DownloadSongService> mDownloadSongServiceConnection;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill_board;
    }

    @Override
    public void initView() {
        translucentStatusBar();
        mTitle = getIntent().getStringExtra(FLAG_TITLE);
        mType = getIntent().getIntExtra(FLAG_TYPE, 0);
        setToolBar(mToolbar, mTitle);
        mRecyclerAdapter = new LoadMoreRecyclerAdapter<Music>(BillboardActivity.this, mRecyclerView, mMusicList, R.layout.media_list_item_2) {
            @Override
            public void convert(final RecyclerViewHolder holder, final Music data, final int position) {

                ImageView imageView = holder.findViewById(R.id.play_eq);
                TextView titleView = holder.findViewById(R.id.title);
                TextView artistView = holder.findViewById(R.id.description);
                ImageView operationView = holder.findViewById(R.id.operation);
                if (data.getImageUrl() != null)
                    Glide.with(BillboardActivity.this).load(data.getImageUrl()).error(R.drawable.ic_launcher).into(imageView);
                titleView.setText(data.getTitle());
                artistView.setText(data.getArtist());
                operationView.setImageResource(R.drawable.ic_keyboard_arrow_down_gray_36dp);
                operationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadSong(data);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayerService playerService = mPlayServiceConnection.getService();
                        if (playerService != null) {
                            playerService.play(mMusicList, position);
                        }
                    }
                });
            }
        };
        mRecyclerAdapter.setOnLoadMoreListener(new LoadMoreRecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPresenter.loadBillboardMusics(Constant.API_FORMAT, Constant.API_CALLBACK, Constant.API_FROM, Constant.API_METHOD_RANK, mType, COUNT, COUNT);
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPlayServiceConnection = new BaseServiceConnection<>();
        mDownloadSongServiceConnection = new BaseServiceConnection<>();
        bindService(PlayerService.class, mPlayServiceConnection);
        bindService(DownloadSongService.class, mDownloadSongServiceConnection);
        mPresenter.loadBillboardMusics(Constant.API_FORMAT, Constant.API_CALLBACK, Constant.API_FROM, Constant.API_METHOD_RANK, mType, COUNT, 0);
    }

    private <T extends BaseService> void bindService(Class<T> cls, BaseServiceConnection<T> connection) {
        Intent intent = new Intent(this, cls);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mPlayServiceConnection);
        unbindService(mDownloadSongServiceConnection);
    }

    public static void startAction(Context context, String title, int type) {
        Intent intent = new Intent(context, BillboardActivity.class);
        intent.putExtra(FLAG_TITLE, title);
        intent.putExtra(FLAG_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public void showBackdrop(String url) {
        Glide.with(this).load(url).into(mImageView);
    }

    @Override
    public void showBillBoardMusics(List<Music> musics) {
        mRecyclerAdapter.setLoading(false);
        mMusicList.addAll(musics);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    private void downloadSong(final Music music) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("下载歌曲")
                .setMessage("是否下载歌曲:" + music.getTitle())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        long songId = music.getId();
                        DownloadSongService downloadSongService = mDownloadSongServiceConnection.getService();
                        if (downloadSongService != null)
                            downloadSongService.downloadSong(songId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    @Override
    public void showLoading(String title) {
        mLoadingTip.setLoadingTip(LoadingTip.LoadStatus.loading);
        mLoadingTip.setTips(title);
    }

    @Override
    public void stopLoading() {
        mLoadingTip.setLoadingTip(LoadingTip.LoadStatus.finish);
    }

    @Override
    public void showErrorTip(String msg) {
        mLoadingTip.setLoadingTip(LoadingTip.LoadStatus.error);
        mLoadingTip.setTips(msg);
    }
}
