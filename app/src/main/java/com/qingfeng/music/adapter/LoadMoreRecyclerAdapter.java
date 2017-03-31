package com.qingfeng.music.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingfeng.music.R;
import com.wgl.android.library.baseadapter.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by Ganlin.Wu on 2016/10/11.
 */
public abstract class LoadMoreRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {

    private boolean isLoading = false;
    private OnLoadMoreListener mOnLoadMoreListener;
    private final static int TYPE_ITEM = 1 << 1;
    private final static int TYPE_PROGRESS = 1 << 2;

    public LoadMoreRecyclerAdapter(Context mContext, RecyclerView recyclerView, List<T> mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
        init(recyclerView);
    }

    private void init(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemCount = linearLayoutManager.getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                /**
                 * dy > 0 表示上滑
                 */
                if (!isLoading && dy > 0 && lastVisibleItemPosition >= itemCount - 1) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setLoading(boolean b) {
        isLoading = b;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            View progressView = LayoutInflater.from(getContext()).inflate(R.layout.progress_item, parent, false);
            ProgressViewHolder progressViewHolder = new ProgressViewHolder(progressView);
            return progressViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
