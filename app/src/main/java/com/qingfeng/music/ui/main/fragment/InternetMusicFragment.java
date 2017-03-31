package com.qingfeng.music.ui.main.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.qingfeng.music.R;
import com.qingfeng.music.ui.billboard.activity.BillboardActivity;
import com.qingfeng.music.ui.main.contract.InternetMainContract;
import com.qingfeng.music.ui.main.model.InternetMainModel;
import com.qingfeng.music.ui.main.presenter.InternetMainPresenter;
import com.wgl.android.library.base.BaseFragment;
import com.wgl.android.library.baseadapter.BaseRecyclerAdapter;
import com.wgl.android.library.viewholder.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Ganlin.Wu on 2016/9/28.
 */
public class InternetMusicFragment extends BaseFragment<InternetMainPresenter, InternetMainModel> implements InternetMainContract.View {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private BaseRecyclerAdapter<String> mRecyclerAdapter;

    private List<String> mTitles = new ArrayList<>();


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_internet_music;
    }

    @Override
    protected void initView() {
        mRecyclerAdapter = new BaseRecyclerAdapter<String>(getContext(), mTitles, R.layout.internet_type_item) {
            @Override
            public void convert(RecyclerViewHolder holder, final String data, final int position) {
                TextView textView = holder.findViewById(R.id.text);
                textView.setText(data);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.loadMusicType(data);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPresenter.loadMusicCategories();
    }

    @Override
    public void showMusicCategories(List<String> musics) {
        mTitles.clear();
        mTitles.addAll(musics);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void startBillBoardAction(String title, int type) {
        BillboardActivity.startAction(getContext(), title, type);
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
}
